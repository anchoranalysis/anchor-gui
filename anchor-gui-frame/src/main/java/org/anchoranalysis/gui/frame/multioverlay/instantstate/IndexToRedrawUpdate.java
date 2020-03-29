package org.anchoranalysis.gui.frame.multioverlay.instantstate;

/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.BoundedIndexBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;
import org.anchoranalysis.image.stack.DisplayStack;

class IndexToRedrawUpdate implements IObjectBridge<Integer, OverlayedDisplayStack> {

	private BoundedIndexBridge<ColoredOverlayedInstantState> delegate;
	private IObjectBridge<Integer,DisplayStack> background;
	
	public IndexToRedrawUpdate(
		IBoundedIndexContainer<ColoredOverlayedInstantState> cntr,
		IObjectBridge<Integer,DisplayStack> background
	) {
		delegate = new BoundedIndexBridge<>(cntr);
		this.background = background;
	}
	
	@Override
	public OverlayedDisplayStack bridgeElement(Integer sourceObject)
			throws GetOperationFailedException {
		
		ColoredOverlayedInstantState found = delegate.bridgeElement(sourceObject);
		
		return new OverlayedDisplayStack(found.getOverlayCollection(), background.bridgeElement(sourceObject) );
	}

	public void setImageStackCntr(
			IObjectBridge<Integer, DisplayStack> imageStackCntr)
			throws SetOperationFailedException {
		this.background = imageStackCntr;
	}
	
}
