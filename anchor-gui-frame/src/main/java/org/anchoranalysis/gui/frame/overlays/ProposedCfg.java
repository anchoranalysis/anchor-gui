/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.gui.frame.overlays;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.conic.RegionMapSingleton;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.image.extent.ImageDimensions;
import lombok.Getter;
import lombok.Setter;

public class ProposedCfg {

    /** The total cfg to be drawn */
    private ColoredOverlayCollection overlays = new ColoredOverlayCollection();
    
    /**
     * The marks that need to be redrawn, as they have changed
     * <p>
     * Optional. The marks that have changed from the previous time (to avoid redrawing everything),
     * otherwise NULL.
     */
    @Getter @Setter private Cfg cfgToRedraw = new Cfg();
    
    /** The core part of the cfg */
    @Getter @Setter private Cfg cfgCore = new Cfg(); 
    
    private ImageDimensions dimensions;

    private ProposerFailureDescription pfd;
    private boolean success = false;

    private boolean hasSuggestedSliceNum = false;
    
    @Getter private int suggestedSliceNum = -1;

    private RegionMembershipWithFlags regionMembership;

    public ProposedCfg() {
        // Do we need to initialise pfd with a default???
        this.regionMembership =
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
    }
    
    public ProposedCfg(ImageDimensions dimensions) {
        this();
        this.dimensions = dimensions;
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
        this.overlays = OverlayCollectionMarkFactory.createColor(cfg, regionMembership);
    }

    public void setColoredCfg(ColoredOverlayCollection cfg) {
        this.overlays = cfg;
    }

    public boolean hasSugestedSliceNum() {
        return hasSuggestedSliceNum;
    }

    public void setSuggestedSliceNum(int suggestedSliceNum) {
        this.hasSuggestedSliceNum = true;
        this.suggestedSliceNum = suggestedSliceNum;
    }

    public ImageDimensions dimensions() {
        return dimensions;
    }
}
