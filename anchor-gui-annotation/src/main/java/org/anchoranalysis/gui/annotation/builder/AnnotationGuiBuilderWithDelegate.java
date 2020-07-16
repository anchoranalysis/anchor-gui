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

public abstract class AnnotationGuiBuilderWithDelegate<T extends AnnotationInitParams,S  extends AnnotatorStrategy> extends AnnotationGuiBuilder<T> {

	private AnnotationWithStrategy<S> delegate;
	
	public AnnotationGuiBuilderWithDelegate(AnnotationWithStrategy<S> delegate) {
		this.delegate = delegate;
	}

	@Override
	public ChangeableBackgroundDefinition backgroundDefinition( AnnotationBackground annotationBackground ) {
		return BackgroundDefinitionCreator.create(annotationBackground, delegate.getStrategy().getBackground());
	}
	
	/** A path that is used to allow the user to delete an annotation */
	@Override
	public Path deletePath() {
		return delegate.getAnnotationPath();
	}
		
	// Cached-operation
	@Override
	public OperationWithProgressReporter<NamedProvider<Stack>,CreateException> stacks() {
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

	protected AnnotationBackground createBackground(ProgressReporterMultiple prm, NamedProvider<Stack> backgroundStacks ) throws CreateException {
		try {
			return new AnnotationBackground(
				prm,
				backgroundStacks,
				getStrategy().getBackground().getStackNameVisualOriginal()
			);
		} catch (GetOperationFailedException e) {
			throw new CreateException(e);
		}
	}


}
