package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.log.LogUtilities;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.plugin.io.bean.input.stack.StackSequenceInput;

public class ExtractTimeSequenceFromInput extends CachedOperationWithProgressReporter<TimeSequenceProvider,CreateException> {

	private StackSequenceInput inputObject;
	private int seriesNum;
	
	public ExtractTimeSequenceFromInput( StackSequenceInput inputObject ) {
		this(inputObject,0);
	}
	
	public ExtractTimeSequenceFromInput( StackSequenceInput inputObject, int seriesNum ) {
		super();
		this.inputObject = inputObject;
		this.seriesNum = seriesNum;
	}
	
	private TimeSequenceProvider doOperationWithException( ProgressReporter progressReporter ) throws CreateException {
		
		try {
			TimeSequence timeSeries = inputObject
				.createStackSequenceForSeries(seriesNum)
				.doOperation(progressReporter);
			
			LazyEvaluationStore<TimeSequence> store = new LazyEvaluationStore<>(
				LogUtilities.createNullErrorReporter(),
				"extractTimeSequence"
			);
			
			store.add(
				"input_stack",
				new IdentityOperation<>(timeSeries)
			);
			
			return new TimeSequenceProvider(
				store,
				inputObject.numFrames()
			);
		} catch (RasterIOException | OperationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	protected TimeSequenceProvider execute( ProgressReporter progressReporter ) throws CreateException {
		return doOperationWithException( progressReporter );
	}
}