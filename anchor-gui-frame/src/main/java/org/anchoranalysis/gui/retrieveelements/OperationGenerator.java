package org.anchoranalysis.gui.retrieveelements;

/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.file.Path;
import java.util.Optional;

import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * 
 * @author Owen Feehan
 *
 * @param <S> generated-type
 * @param <T> iteration-type
 */
class OperationGenerator<S,T> extends ObjectGenerator<S> implements IterableObjectGenerator<Operation<T,AnchorNeverOccursException>, S> {

	private IterableObjectGenerator<T, S> delegate;

	private Operation<T,AnchorNeverOccursException> element;
	
	public OperationGenerator(
			IterableObjectGenerator<T, S> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setIterableElement(Operation<T,AnchorNeverOccursException> element)
			throws SetOperationFailedException {
		this.element = element;
		delegate.setIterableElement(
			element.doOperation()
		);
	}
	
	@Override
	public Operation<T,AnchorNeverOccursException> getIterableElement() {
		return element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}

	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public ObjectGenerator<S> getGenerator() {
		return delegate.getGenerator();
	}

	@Override
	public S generate() throws OutputWriteFailedException {
		return delegate.getGenerator().generate();
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
		delegate.getGenerator().writeToFile(outputWriteSettings, filePath);
		
	}

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return delegate.getGenerator().getFileExtension(outputWriteSettings);
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return delegate.getGenerator().createManifestDescription();
	}
	
	
}
