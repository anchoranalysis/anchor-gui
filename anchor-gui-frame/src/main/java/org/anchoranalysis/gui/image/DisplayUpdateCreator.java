package org.anchoranalysis.gui.image;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.BoundColoredOverlayCollection;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.image.stack.DisplayStack;

/**
 * Creates DisplayUpdates from ColoredCfgRedrawUpdate by applying them to a overlayedDisplayStack
 * 
 * @author Owen Feehan
 *
 */
public class DisplayUpdateCreator implements FunctionWithException<Integer, DisplayUpdate,OperationFailedException> {

	private FunctionWithException<Integer,OverlayedDisplayStackUpdate,GetOperationFailedException> src;
	private DrawOverlay maskWriter;
	private IDGetter<Overlay> idGetter;
	
	// This keeps track of the current over
	private BoundColoredOverlayCollection boundOverlay = null;
	
	public DisplayUpdateCreator( FunctionWithException<Integer,OverlayedDisplayStackUpdate,GetOperationFailedException> src, IDGetter<Overlay> idGetter	) {
		super();
		this.src = src;
		this.idGetter = idGetter;
	}

	// Must be called before we can bridge any elements
	public void updateMaskWriter( DrawOverlay maskWriter ) throws SetOperationFailedException {
		this.maskWriter = maskWriter;
		if (boundOverlay!=null) {
			boundOverlay.updateMaskWriter(maskWriter);
		}
	}

	@Override
	public DisplayUpdate apply(Integer sourceObject) throws OperationFailedException {
		try {
			OverlayedDisplayStackUpdate update = src.apply(sourceObject);
			
			if (update==null) {
				// No change so we return the existing stack
				return null;
			}
			
			//System.out.println("OverlayBridge new Element\n");

			// If we haven't a current background yet, then we assume the first update has a BackgroundStack attached 
			if (boundOverlay==null) {
				assert(update.getBackgroundStack()!=null);
				DisplayStack currentBackground = update.getBackgroundStack();
				boundOverlay = new BoundColoredOverlayCollection( maskWriter, idGetter, currentBackground.getDimensions() );
			}
						
			return update.applyAndCreateDisplayUpdate(boundOverlay);

		} catch (CreateException | GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
		
	}

	public OverlayRetriever getOverlayRetriever() {
		return boundOverlay.getPrecalculatedCache();
	}
}
