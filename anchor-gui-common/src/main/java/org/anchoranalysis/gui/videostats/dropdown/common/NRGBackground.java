package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSet;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

/** 
 * NRGStack and background together
 * 
 * A background will always exist
 * 
 * An nrg-stack may not be defined, in which case a *guess* can be made, if necessary.
 * 
 */
public class NRGBackground {

	private OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> opBackgroundSet;
	private Operation<NRGStackWithParams,OperationFailedException> opNrgStack;
	private OperationWithProgressReporter<Integer,OperationFailedException> opNumFrames;
	
	private NRGBackground(
		OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> opBackgroundSet,
		Operation<NRGStackWithParams,OperationFailedException> opNrgStack,
		OperationWithProgressReporter<Integer,OperationFailedException> opNumFrames
	) {
		super();
		this.opBackgroundSet = opBackgroundSet;
		this.opNrgStack = opNrgStack;
		this.opNumFrames = opNumFrames;
	}
	
	private NRGBackground(
		OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> opNrgStack,
		OperationWithProgressReporter<Integer,OperationFailedException> opNumFrames
	) {
		super();
		this.opBackgroundSet = opBackgroundSet;
		this.opNrgStack = removeProgressReporter( opNrgStack );
		this.opNumFrames = opNumFrames;
		assert( opNumFrames!= null );
	}
	
	public static NRGBackground createFromBackground(
		OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> opNrgStack		
	) {
		return new NRGBackground(
			opBackgroundSet,
			opNrgStack,
			new IdentityOperationWithProgressReporter<>(1)
		);
	}
	
	public static <E extends Throwable> NRGBackground createStack(
		OperationWithProgressReporter<INamedProvider<Stack>,E> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> opNrgStack
	) {
		OperationWithProgressReporter<TimeSequenceProvider,E> opConvert = progressReporter -> { 
			return new TimeSequenceProvider(
				new WrapStackAsTimeSequence( opBackgroundSet.doOperation(progressReporter) ),
				1
			);
		};
		return createStackSequence( opConvert, opNrgStack );
	}
	
	public static NRGBackground createStackSequence(
		OperationWithProgressReporter<TimeSequenceProvider,? extends Throwable> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> opNrgStack
	) {
		return new NRGBackground(
			convertProvider(opBackgroundSet),
			opNrgStack,
			progress -> {
				try {
					return opBackgroundSet.doOperation(progress).getNumFrames();
				} catch (Throwable e) {
					throw new OperationFailedException(e);
				}
			}
		); 
	}

	public OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> getBackgroundSet() {
		return opBackgroundSet;
	}
	
	public IAddVideoStatsModule addNrgStackToAdder( IAddVideoStatsModule adder ) {
		return new AdderAppendNRGStack(adder, convertToGetter() );
	}
	
	// Assumes the number of frames does not change
	public NRGBackground copyChangeOp( OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> opBackgroundSetNew ) {
		return new NRGBackground(opBackgroundSetNew, opNrgStack, opNumFrames);
	}
	
	private INRGStackGetter convertToGetter() {
		return () -> opNrgStack.doOperation();
	}
	
	private static Operation<NRGStackWithParams,OperationFailedException> removeProgressReporter(
		OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> in
	) {
		return () -> {
			return in.doOperation( ProgressReporterNull.get() );
		};
	}
	
	private static OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> convertProvider(
		OperationWithProgressReporter<TimeSequenceProvider,? extends Throwable> in
	) {
		return new OperationCreateBackgroundSet(in);
	}

	public Operation<NRGStackWithParams,OperationFailedException> getNrgStack() {
		return opNrgStack;
	}
	
	public int numFrames() throws OperationFailedException {
		return opNumFrames.doOperation( ProgressReporterNull.get() );
	}
	
	
	public String arbitraryBackgroundStackName() throws InitException {
		try {
			return getBackgroundSet().doOperation( ProgressReporterNull.get() )
				.names()
				.iterator()
				.next();
			
		} catch (GetOperationFailedException e) {
			throw new InitException(e);
		}			
	}
}
