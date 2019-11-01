package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

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

import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.frame.overlays.RedrawUpdate;

import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.OverlayedDisplayStackUpdate;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import overlay.OverlayCollectionMarkFactory;

public class RedrawUpdateFromProposal {
	
	public static RedrawUpdate apply( ProposedCfg er, Cfg bboxRedraw ) {
		return new RedrawUpdate(
			selectUpdate(er, bboxRedraw),
			overlaysForTrigger(er),
			suggestedSliceNum(er)
		);
	}

	private static OverlayedDisplayStackUpdate selectUpdate( ProposedCfg cfg, Cfg bboxRedraw ) {
		ColoredOverlayCollection oc = cfg.getColoredCfg();
		if (bboxRedraw!=null) {
			OverlayCollection ocRedraw = OverlayCollectionMarkFactory.createWithoutColor(
				bboxRedraw,
				cfg.getRegionMembership()
			);
			return OverlayedDisplayStackUpdate.updateOverlaysWithSimilar(oc, ocRedraw);
		} else {
			return OverlayedDisplayStackUpdate.updateOverlaysWithSimilar(oc);
		}
	}

	// HACK
	// Let's just always send the fist item
	// We create a cfg with just the first item
	private static OverlayCollection overlaysForTrigger( ProposedCfg er ) {
		// We the marks back from the overlays
		//Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( er.getColoredCfg().getOverlayCollection() );
		
		OverlayCollection ocFirst = new OverlayCollection();
		
		if (er.isSuccess() && er.getColoredCfg().getOverlayCollection().size()>=1) {
			ocFirst.add( er.getColoredCfg().getOverlayCollection().get(0) );
		}
		
		return ocFirst;
	}
	
	private static int suggestedSliceNum( ProposedCfg er) {
		// Slice Nums
		if (er.isSuccess() && er.hasSugestedSliceNum()) {
			return er.getSuggestedSliceNum();
		} else {
			return -1;
		}
	}
	
}
