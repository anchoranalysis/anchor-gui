/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.CachedOperationWrap;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.image.stack.Stack;

public abstract class AnnotationGuiBuilder<T extends AnnotationInitParams> {

    private CachedOperation<AnnotationSummary, CreateException> queryAnnotationStatus;

    public AnnotationGuiBuilder() {
        super();
        this.queryAnnotationStatus = createCachedSummary();
    }

    /** Should the user be prompted to use a default annotation? */
    public abstract boolean isUseDefaultPromptNeeded();

    public abstract T createInitParams(
            ProgressReporterMultiple prm,
            AnnotationGuiContext context,
            Logger logger,
            boolean useDefaultCfg)
            throws CreateException;

    /**
     * A mechanism for exporting annotations
     *
     * @return mechanism or NULL if it isn't supported
     */
    public abstract ExportAnnotation exportAnnotation();

    public abstract PanelNavigation createInitPanelNavigation(
            T paramsInit, AnnotationPanelParams params, AnnotationFrameControllers controllers);

    public abstract void showAllAdditional(T paramsInit, AdditionalFramesContext context)
            throws OperationFailedException;

    /**
     * A cached annotation-summary for the annotation
     *
     * <p>The cache should be reset of the annotation is changed by the user
     */
    public CachedOperation<AnnotationSummary, CreateException> queryAnnotationSummary() {
        return queryAnnotationStatus;
    }

    public abstract ChangeableBackgroundDefinition backgroundDefinition(
            AnnotationBackground annotationBackground);

    /** A path that is used to allow the user to delete an annotation */
    public abstract Path deletePath();

    // Cached-operation
    public abstract OperationWithProgressReporter<NamedProvider<Stack>, CreateException> stacks();

    public abstract String descriptiveName();

    public abstract Optional<File> associatedFile();

    /** Height in pixels of the panel that isn't the 'image' section of the frame */
    public abstract int heightNonImagePanel();

    /** Creates an annotation summary for the current-annotation */
    protected abstract AnnotationSummary createSummary() throws CreateException;

    /** Creates a cached version of the createSummary() method */
    private CachedOperation<AnnotationSummary, CreateException> createCachedSummary() {
        return new CachedOperationWrap<>(() -> createSummary());
    }
}
