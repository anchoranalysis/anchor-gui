package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;

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


import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.GetOverlayCollection;

/**
 * Triggers redraw updates in response to changes in the currently selected overlay
 * 
 * The bounding boxes around the overlays are selected.
 * 
 * @author Owen Feehan
 *
 */
class RedrawFromCfgGetter implements PropertyValueChangeListener<IntArray> {
	
	// Gives us the currently selected marks
	private GetOverlayCollection cfgGetter;
	private IRedrawable redrawable;
	private ColoredOverlayCollection old;
	
	public RedrawFromCfgGetter(GetOverlayCollection cfgGetter, IRedrawable redrawable, Logger logger ) {
		super();
		assert(cfgGetter!=null);
		assert(redrawable!=null);
		this.cfgGetter = cfgGetter;
		this.redrawable = redrawable;
	}

	@Override
	public synchronized void propertyValueChanged(PropertyValueChangeEvent<IntArray> evt) {
		
		ColoredOverlayCollection cfgNew = cfgGetter.getOverlays();
		
		if (old==null) {
			
			// TODO THIS IS A HACK TO SOLVE, WE CAN MAKE THIS MORE EFFICIENT
			
			// change to trigger a full redraw
			// and draw with a particular cfg
			//redrawable.redrawAll();
			
			redrawable.applyRedrawUpdate( OverlayedDisplayStackUpdate.updateChanged( cfgNew.withoutColor() ));
			
			//redrawable.redraw(cfg)
			
			//cfgGenerator.redraw( cfgNew );
			//cfgGenerator.generate();
			//cfgGenerator.redrawAll();
		} else {
			OverlayCollection merged = old.withoutColor().createMerged(cfgNew.withoutColor());
			//redrawable.redrawParts(  );
			
			assert( merged!=null );
			redrawable.applyRedrawUpdate( OverlayedDisplayStackUpdate.updateChanged( merged ));
			
			//cnvtr.update(old, bboxList);

			
		}

		old = cfgNew;
	
	}
	

	
	

}