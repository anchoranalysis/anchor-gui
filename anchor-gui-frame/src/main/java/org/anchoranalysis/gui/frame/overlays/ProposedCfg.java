package org.anchoranalysis.gui.frame.overlays;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

public class ProposedCfg {

	private ColoredOverlayCollection overlays = new ColoredOverlayCollection();				// The total cfg to be drawn
	private Cfg cfgToRedraw = new Cfg();		// The marks that need to be redrawn, as they have changed
	private Cfg cfgCore = new Cfg();	// The core part of the cfg
	private ImageDim dim;
	
	private ProposerFailureDescription pfd;
	private boolean success = false;
	
	private boolean hasSuggestedSliceNum = false;
	private int suggestedSliceNum = -1;
	
	private RegionMembershipWithFlags regionMembership;
	
	public ProposedCfg() {
		// Do we need to initialise pfd with a default???
		this.regionMembership = RegionMapSingleton.instance()
				.membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE );
	}
	
	public RegionMembershipWithFlags getRegionMembership() {
		return regionMembership;
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
		return overlays;
	}
	
	public void setColoredCfg(ColoredCfg cfg) {
		this.overlays = OverlayCollectionMarkFactory.createColor(
			cfg,
			regionMembership
		);
	}
	
	public void setColoredCfg(ColoredOverlayCollection cfg) {
		this.overlays = cfg;
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
