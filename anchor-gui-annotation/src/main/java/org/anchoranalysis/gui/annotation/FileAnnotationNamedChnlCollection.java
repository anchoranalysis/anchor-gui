/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.dropdown.AnnotationDropDown;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class FileAnnotationNamedChnlCollection extends InteractiveFile {

    private AnnotationGuiBuilder<?> annotation;
    private VideoStatsModuleGlobalParams mpg;
    private AnnotationRefresher annotationRefresher;
    private CachedOperation<AnnotationSummary, ? extends Throwable> op;
    private MarkEvaluatorManager markEvaluatorManager;

    public FileAnnotationNamedChnlCollection(
            AnnotationGuiBuilder<?> annotation,
            AnnotationRefresher annotationRefresher,
            MarkEvaluatorManager markEvaluatorManager,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.annotation = annotation;
        this.annotationRefresher = annotationRefresher;
        this.mpg = mpg;
        this.markEvaluatorManager = markEvaluatorManager;

        op = annotation.queryAnnotationSummary();

        try {
            op.doOperation();
        } catch (Throwable e) {
            mpg.getLogger().errorReporter().recordError(FileAnnotationNamedChnlCollection.class, e);
        }
    }

    @Override
    public String identifier() {
        return annotation.descriptiveName();
    }

    private void invalidateProgressState() {
        op.reset();
    }

    public AnnotationSummary summary() {
        try {
            return op.doOperation();
        } catch (Throwable e) {
            mpg.getLogger().errorReporter().recordError(FileAnnotationNamedChnlCollection.class, e);
            return null;
        }
    }

    @Override
    public String type() {
        return FileAnnotationNamedChnlCollection.class.getSimpleName();
    }

    @Override
    public OpenedFile open(
            IAddVideoStatsModule globalSubgroupAdder, BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException {

        AnnotationRefresher refresherResetCache =
                new AnnotationRefresher() {

                    @Override
                    public void refreshAnnotation() {
                        invalidateProgressState();
                        annotationRefresher.refreshAnnotation();
                    }
                };

        AnnotationDropDown dropDown =
                new AnnotationDropDown(
                        annotation,
                        new AnnotationGuiContext(refresherResetCache, markEvaluatorManager),
                        identifier());

        try {
            dropDown.init(globalSubgroupAdder, outputManager, mpg);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }

    @Override
    public Optional<File> associatedFile() {
        return annotation.associatedFile();
    }
}
