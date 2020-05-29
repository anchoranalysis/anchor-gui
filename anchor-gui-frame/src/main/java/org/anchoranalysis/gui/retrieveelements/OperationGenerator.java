package org.anchoranalysis.gui.retrieveelements;

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
