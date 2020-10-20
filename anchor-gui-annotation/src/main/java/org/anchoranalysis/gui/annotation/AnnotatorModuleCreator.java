/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.annotation;

import java.awt.Component;
import javax.swing.JOptionPane;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.builder.AdditionalFramesContext;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
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

    private boolean useDefaultMarks = false;

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

            useDefaultMarks = (dialogResult == JOptionPane.YES_OPTION);
        }
    }

    @Override
    public void doInBackground(ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {
        super.doInBackground(progressReporter);

        try (ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, 1)) {

            try {
                paramsInit =
                        annotation.createInitParams(prm, context, mpg.getLogger(), useDefaultMarks);
            } catch (CreateException e) {
                throw new VideoStatsModuleCreateException(e);
            }
            prm.incrWorker();
        }
    }

    @Override
    public void createAndAddVideoStatsModule(AddVideoStatsModule adder)
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

    private void configureAddImageFrame(AddVideoStatsModule adder)
            throws InitException, VideoStatsModuleCreateException {

        InternalFrameAnnotator imageFrame =
                new InternalFrameAnnotator(name, mpg.getLogger().errorReporter());

        paramsInit
                .getBackground()
                .configureLinkManager(
                        adder.getSubgroup().getDefaultModuleState().getLinkStateManager());

        // Here we optionally set an adder to send back energy-stacks
        SliderState sliderState =
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
            InternalFrameAnnotator imageFrame, AnnotationBackgroundInstance background) {

        ControllerPopupMenuWithBackground controller = imageFrame.controllerBackgroundMenu();
        controller.addDefinition(mpg, annotation.backgroundDefinition(background));
    }
}
