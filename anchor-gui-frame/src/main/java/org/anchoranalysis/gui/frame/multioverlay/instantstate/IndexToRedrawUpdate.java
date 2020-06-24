package org.anchoranalysis.gui.frame.multioverlay.instantstate;



import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.BoundedIndexBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;
import org.anchoranalysis.image.stack.DisplayStack;

class IndexToRedrawUpdate implements FunctionWithException<Integer, OverlayedDisplayStack,GetOperationFailedException> {

	private BoundedIndexBridge<ColoredOverlayedInstantState> delegate;
	private FunctionWithException<Integer,DisplayStack,GetOperationFailedException> background;
	
	public IndexToRedrawUpdate(
		IBoundedIndexContainer<ColoredOverlayedInstantState> cntr,
		FunctionWithException<Integer,DisplayStack, GetOperationFailedException> background
	) {
		delegate = new BoundedIndexBridge<>(cntr);
		this.background = background;
	}
	
	@Override
	public OverlayedDisplayStack apply(Integer sourceObject) throws GetOperationFailedException {
		
		ColoredOverlayedInstantState found = delegate.apply(sourceObject);
		
		return new OverlayedDisplayStack(
			found.getOverlayCollection(),
			background.apply(sourceObject)
		);
	}

	public void setImageStackCntr(
			FunctionWithException<Integer, DisplayStack, GetOperationFailedException> imageStackCntr)
			throws SetOperationFailedException {
		this.background = imageStackCntr;
	}
	
}
