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
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;

import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.instantstate.OverlayedInstantState;

class AddColorBridge implements IObjectBridge<OverlayedInstantState, ColoredOverlayedInstantState> {

	private ColorIndex colorIndex;
	private IDGetter<Overlay> colorIDGetter;
	
	public AddColorBridge(ColorIndex colorIndex,
			IDGetter<Overlay> colorIDGetter) {
		super();
		this.colorIndex = colorIndex;
		this.colorIDGetter = colorIDGetter;
	}

	@Override
	public ColoredOverlayedInstantState bridgeElement(OverlayedInstantState sourceObject)
			throws GetOperationFailedException {
		
		OverlayCollection oc = sourceObject.getOverlayCollection(); 
		
		ColoredOverlayCollection coc = new ColoredOverlayCollection(
			oc,
			createColorListForOverlays( oc )
		);
		
		return new ColoredOverlayedInstantState( sourceObject.getIndex(), coc );
	}
	
	private ColorList createColorListForOverlays( OverlayCollection oc ) {
		
		ColorList colorList = new ColorList();
		
		for( int i=0; i<oc.size(); i++) {
			Overlay ol = oc.get(i); 
			colorList.add( colorIndex.get( colorIDGetter.getID(ol, i)) );
		}
		
		return colorList;
	}
	
}
