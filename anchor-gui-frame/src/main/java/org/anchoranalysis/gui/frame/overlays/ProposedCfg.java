package org.anchoranalysis.gui.frame.overlays;

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


import overlay.OverlayCollectionMarkFactory;

import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

public class ProposedCfg {

	private ColoredOverlayCollection cfg = new ColoredOverlayCollection();				// The total cfg to be drawn
	private Cfg cfgToRedraw = new Cfg();		// The marks that need to be redrawn, as they have changed
	private Cfg cfgCore = new Cfg();	// The core part of the cfg
	private ImageDim dim;
	
	private ProposerFailureDescription pfd;
	private boolean success = false;
	
	private boolean hasSuggestedSliceNum = false;
	private int suggestedSliceNum = -1;
	
	public ProposedCfg() {
		// Do we need to initialise pfd with a default???
	}
	
	public ProposerFailureDescription getPfd() {
		return pfd;
	}
	public void setPfd(ProposerFailureDescription pfd) {
		this.pfd = pfd;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public ColoredOverlayCollection getColoredCfg() {
		return cfg;
	}
	
	public void setColoredCfg(ColoredCfg cfg) {
		this.cfg = OverlayCollectionMarkFactory.createColor(cfg);
	}
	
	public void setColoredCfg(ColoredOverlayCollection cfg) {
		this.cfg = cfg;
	}
	
	public boolean hasSugestedSliceNum() {
		return hasSuggestedSliceNum;
	}
	
	public int getSuggestedSliceNum() {
		return suggestedSliceNum;
	}
	
	public void setSuggestedSliceNum( int suggestedSliceNum ) {
		this.hasSuggestedSliceNum = true;
		this.suggestedSliceNum = suggestedSliceNum;
	}

	// Optional. The marks that have changed from the previous time (to avoid redrawing everything), otherwise NULL.
	public Cfg getCfgToRedraw() {
		return cfgToRedraw;
	}

	public void setCfgToRedraw(Cfg cfgToRedraw) {
		this.cfgToRedraw = cfgToRedraw;
	}

	public Cfg getCfgCore() {
		return cfgCore;
	}

	public void setCfgCore(Cfg cfgCore) {
		this.cfgCore = cfgCore;
	}

	public ImageDim getDimensions() {
		return dim;
	}

	public void setDimensions(ImageDim dim) {
		this.dim = dim;
	}
}
