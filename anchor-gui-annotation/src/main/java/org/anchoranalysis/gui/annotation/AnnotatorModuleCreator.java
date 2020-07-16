/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.awt.Component;
import javax.swing.JOptionPane;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.builder.AdditionalFramesContext;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.InternalFrameAnnotator;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public class AnnotatorModuleCreator<T extends AnnotationInitParams>
        extends VideoStatsModuleCreator {

    private AnnotationGuiBuilder<T> annotation;

    private OutputWriteSettings outputWriteSettings;
    private VideoStatsModuleGlobalParams mpg;

    private AnnotationGuiContext context;
    private String name;

    private boolean useDefaultCfg = false;

    // Transient
    private T paramsInit;

    public AnnotatorModuleCreator(
            String name,
            AnnotationGuiBuilder<T> annotation,
            AnnotationGuiContext context,
            OutputWriteSettings outputWriteSettings,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.name = name;
        this.annotation = annotation;
        this.context = context;
        this.outputWriteSettings = outputWriteSettings;
        this.mpg = mpg;
    }

    @Override
    public void beforeBackground(Component parentComponent) {
        super.beforeBackground(parentComponent);

        if (annotation.isUseDefaultPromptNeeded()) {

            int dialogResult =
                    JOptionPane.showConfirmDialog(
                            parentComponent,
                            "Would you like to load a default annotation?",
                            "Load default annotation?",
                            JOptionPane.YES_NO_OPTION);

            useDefaultCfg = (dialogResult == JOptionPane.YES_OPTION);
        }
    }

    @Override
    public void doInBackground(ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {
        super.doInBackground(progressReporter);

        try (ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, 1)) {

            try {
                paramsInit =
                        annotation.createInitParams(prm, context, mpg.getLogger(), useDefaultCfg);
            } catch (CreateException e) {
                throw new VideoStatsModuleCreateException(e);
            }
            prm.incrWorker();
        }
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            adder = adder.createChild();

            configureAddImageFrame(adder);

            AdditionalFramesContext context =
                    new AdditionalFramesContext(adder, name, mpg, outputWriteSettings);

            annotation.showAllAdditional(paramsInit, context);

        } catch (VideoStatsModuleCreateException | InitException | OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(AnnotatorModuleCreator.class, e);
        }
    }

    private void configureAddImageFrame(IAddVideoStatsModule adder)
            throws InitException, VideoStatsModuleCreateException {

        InternalFrameAnnotator imageFrame =
                new InternalFrameAnnotator(name, mpg.getLogger().errorReporter());

        paramsInit
                .getBackground()
                .configureLinkManager(
                        adder.getSubgroup().getDefaultModuleState().getLinkStateManager());

        // Here we optionally set an adder to send back nrg_stacks
        ISliderState sliderState =
                imageFrame.init(
                        annotation,
                        paramsInit,
                        adder.getSubgroup().getDefaultModuleState().getState(),
                        outputWriteSettings,
                        mpg);

        addBackgroundMenu(imageFrame, paramsInit.getBackground());

        ModuleAddUtilities.add(adder, imageFrame.moduleCreator(), sliderState);
    }

    private void addBackgroundMenu(
            InternalFrameAnnotator imageFrame, AnnotationBackground background) {

        ControllerPopupMenuWithBackground controller = imageFrame.controllerBackgroundMenu();
        controller.addDefinition(mpg, annotation.backgroundDefinition(background));
    }
}
