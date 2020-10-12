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

package org.anchoranalysis.gui.annotation.builder;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.AnnotationBackgroundInstance;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackground;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.image.stack.NamedStacksSupplier;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.exception.InputReadFailedException;

@RequiredArgsConstructor
public abstract class AnnotationGuiBuilderWithDelegate<
                T extends AnnotationInitParams, S extends AnnotatorStrategy>
        extends AnnotationGuiBuilder<T> {

    private final AnnotationWithStrategy<S> delegate;

    @Override
    public ChangeableBackground backgroundDefinition(
            AnnotationBackgroundInstance annotationBackground) {
        return BackgroundInstanceCreator.create(
                annotationBackground, delegate.getStrategy().getBackground());
    }

    /** A path that is used to allow the user to delete an annotation */
    @Override
    public Path deletePath() {
        return delegate.getPath();
    }

    // Cached-operation
    @Override
    public NamedStacksSupplier stacks() {
        return delegate.stacks();
    }

    @Override
    public String inputName() {
        return delegate.name();
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

    protected Path pathForBindingRequired() throws InputReadFailedException {
        return delegate.pathForBindingRequired();
    }

    protected Path annotationPath() {
        return delegate.getPath();
    }

    protected AnnotationBackgroundInstance createBackground(
            ProgressReporterMultiple prm, NamedProvider<Stack> backgroundStacks)
            throws CreateException {
        try {
            return new AnnotationBackgroundInstance(
                    prm,
                    backgroundStacks,
                    getStrategy().getBackground().getStackNameVisualOriginal());
        } catch (BackgroundStackContainerException e) {
            throw new CreateException(e);
        }
    }
}
