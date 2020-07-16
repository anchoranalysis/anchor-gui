/* (C)2020 */
package org.anchoranalysis.gui.annotation.dropdown;

import javax.swing.JFrame;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class AnnotationDropDown {

    private BoundVideoStatsModuleDropDown delegate;
    private String name;
    private AnnotationGuiBuilder<?> annotation;
    private AnnotationGuiContext context;

    // A dropdown menu representing a particular manifest
    public AnnotationDropDown(
            AnnotationGuiBuilder<?> annotation, AnnotationGuiContext context, String name) {
        this.delegate = new BoundVideoStatsModuleDropDown(name, "/toolbarIcon/rectangle.png");
        this.name = name;
        this.annotation = annotation;
        this.context = context;
    }

    public void init(
            IAddVideoStatsModule adder,
            BoundOutputManagerRouteErrors outputManager,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        addAnnotation(adder, outputManager.getOutputWriteSettings(), mpg);

        // Add Channel Viewer
        addChannelViewer(adder, mpg);

        delegate.getRootMenu().addSeparator();

        addDelete(adder.getParentFrame());
        addExport(adder.getParentFrame());
    }

    private void addAnnotation(
            IAddVideoStatsModule adder, OutputWriteSettings ows, VideoStatsModuleGlobalParams mpg)
            throws InitException {
        String desc = String.format("Annotator: %s", name);

        VideoStatsModuleCreator moduleCreator =
                new AnnotatorModuleCreator<>(desc, annotation, context, ows, mpg);

        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(
                        new IdentityOperationWithProgressReporter<>(adder), moduleCreator);
        delegate.getRootMenu()
                .addAsDefault(
                        new VideoStatsOperationFromCreatorAndAdder(
                                "Annotator",
                                creatorAndAdder,
                                mpg.getThreadPool(),
                                mpg.getLogger()));
    }

    private void addDelete(JFrame frame) {
        delegate.getRootMenu()
                .add(
                        new DeleteAnnotationOperation(
                                frame, annotation.deletePath(), context.getAnnotationRefresher()));
    }

    private void addExport(JFrame frame) {
        ExportAnnotation ea = annotation.exportAnnotation();
        if (ea != null) {
            delegate.getRootMenu()
                    .add(
                            new ExportAnnotationOperation(
                                    frame, ea, context.getAnnotationRefresher()));
        }
    }

    private void addChannelViewer(IAddVideoStatsModule adder, VideoStatsModuleGlobalParams mpg) {
        OperationCreateBackgroundSetWithAdder operationBwsa =
                new OperationCreateBackgroundSetWithAdder(
                        NRGBackground.createStack(annotation.stacks(), null),
                        adder,
                        mpg.getThreadPool(),
                        mpg.getLogger().errorReporter());

        DropDownUtilitiesRaster.addRaster(
                delegate.getRootMenu(),
                delegate,
                operationBwsa.nrgBackground(),
                "Channel Viewer",
                mpg,
                false);
    }

    public IOpenedFileGUI openedFileGUI() {
        return delegate.openedFileGUI();
    }

    public String getName() {
        return delegate.getName();
    }
}
