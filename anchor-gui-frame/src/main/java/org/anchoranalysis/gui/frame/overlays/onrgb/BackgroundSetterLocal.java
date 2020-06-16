package org.anchoranalysis.gui.frame.overlays.onrgb;



import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.image.stack.DisplayStack;

class BackgroundSetterLocal implements IBackgroundSetter {
	
	private IRedrawable redrawable;
	
	public BackgroundSetterLocal( IRedrawable redrawable) {
		super();
		this.redrawable = redrawable;
	}

	@Override
	public void setImageStackCntr(
		FunctionWithException<Integer, DisplayStack,GetOperationFailedException> imageStackCntr
	) throws SetOperationFailedException {
		
		
		DisplayStack stack;
		try {
			stack = imageStackCntr.apply(0);
		} catch (GetOperationFailedException e) {
			throw new SetOperationFailedException(e);
		}
		
		redrawable.applyRedrawUpdate(
			OverlayedDisplayStackUpdate.assignBackground(stack)
		);
		
	}
}
