package org.anchoranalysis.gui.frame.threaded.overlay;

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

import java.util.function.Supplier;

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;

import ch.ethz.biol.cell.imageprocessing.io.generator.raster.OverlayedDisplayStackUpdate;

// Finds ColoredCfgRedrawUpdate which implement changes to existing ColoredCfg
class FindCorrectUpdate implements IObjectBridge<Integer,OverlayedDisplayStackUpdate> {
	private int oldIndex = -1;
	
	private final IObjectBridge<Integer,OverlayedDisplayStack> integerToOverlayedBridge;
	
	private Supplier<Boolean> funcHasBeenInit;
	private IGetClearUpdate getClearUpdate;
	
	public FindCorrectUpdate(
		IObjectBridge<Integer, OverlayedDisplayStack> integerToOverlayedBridge,
		Supplier<Boolean> funcHasBeenInit,
		IGetClearUpdate getClearUpdate
	) {
		super();
		this.funcHasBeenInit = funcHasBeenInit;
		this.integerToOverlayedBridge = integerToOverlayedBridge;
		this.getClearUpdate = getClearUpdate;
	}

	@Override
	public OverlayedDisplayStackUpdate bridgeElement(Integer sourceObject)
			throws GetOperationFailedException {

		//System.out.printf("ThreadedCfgToRGB bridge element\n");
		
		// If our index hasn't changed, then we just apply whatever local updates are queued for processing
		if (funcHasBeenInit.get() && sourceObject.equals(oldIndex)) {
			//System.out.printf("getAndClear\n");
			OverlayedDisplayStackUpdate update = getClearUpdate.getAndClearWaitingUpdate();
			return update;
		} else {
			// Otherwise we apply an external update, and calculate what areas need to be refreshed
			//System.out.printf("externalUpdate\n");
			
			OverlayedDisplayStack ods = integerToOverlayedBridge.bridgeElement(sourceObject);
			oldIndex = sourceObject;
			
			//ColoredOverlayCollection oc = OverlayUtilities.createColor( coloredCfg.getCfg());
			
			OverlayedDisplayStackUpdate update = OverlayedDisplayStackUpdate.assignOverlaysAndBackground(
				ods.getColoredOverlayCollection(),
				ods.getDisplayStack()
			);
			
			getClearUpdate.clearWaitingUpdate();
			return update;
		}
	}
	
}
