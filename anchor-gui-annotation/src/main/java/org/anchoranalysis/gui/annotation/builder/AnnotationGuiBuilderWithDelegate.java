/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.error.AnchorIOException;

public abstract class AnnotationGuiBuilderWithDelegate<
                T extends AnnotationInitParams, S extends AnnotatorStrategy>
        extends AnnotationGuiBuilder<T> {

    private AnnotationWithStrategy<S> delegate;

    public AnnotationGuiBuilderWithDelegate(AnnotationWithStrategy<S> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ChangeableBackgroundDefinition backgroundDefinition(
            AnnotationBackground annotationBackground) {
        return BackgroundDefinitionCreator.create(
                annotationBackground, delegate.getStrategy().getBackground());
    }

    /** A path that is used to allow the user to delete an annotation */
    @Override
    public Path deletePath() {
        return delegate.getAnnotationPath();
    }

    // Cached-operation
    @Override
    public OperationWithProgressReporter<NamedProvider<Stack>, CreateException> stacks() {
        return delegate.stacks();
    }

    @Override
    public String descriptiveName() {
        return delegate.descriptiveName();
    }

    @Override
    public Optional<File> associatedFile() {
        return delegate.associatedFile();
    }

    protected S getStrategy() {
        return delegate.getStrategy();
    }

    protected Optional<Path> pathForBinding() {
        return delegate.pathForBinding();
    }

    protected Path pathForBindingRequired() throws AnchorIOException {
        return delegate.pathForBindingRequired();
    }

    protected Path annotationPath() {
        return delegate.getAnnotationPath();
    }

    protected AnnotationBackground createBackground(
            ProgressReporterMultiple prm, NamedProvider<Stack> backgroundStacks)
            throws CreateException {
        try {
            return new AnnotationBackground(
                    prm,
                    backgroundStacks,
                    getStrategy().getBackground().getStackNameVisualOriginal());
        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
