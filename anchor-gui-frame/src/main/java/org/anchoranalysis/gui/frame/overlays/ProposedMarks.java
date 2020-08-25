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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.ColoredMarks;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;

public class ProposedMarks {

    /** The total marks to be drawn */
    private ColoredOverlayCollection overlays = new ColoredOverlayCollection();

    /**
     * The marks that need to be redrawn, as they have changed
     *
     * <p>Optional. The marks that have changed from the previous time (to avoid redrawing
     * everything), otherwise NULL.
     */
    @Getter @Setter private MarkCollection marksToRedraw = new MarkCollection();

    /** The core part of the marks */
    @Getter @Setter private MarkCollection marksCore = new MarkCollection();

    private Dimensions dimensions;

    private ProposerFailureDescription pfd;
    private boolean success = false;

    private boolean hasSuggestedSliceNum = false;

    @Getter private int suggestedSliceNum = -1;

    private RegionMembershipWithFlags regionMembership;

    public ProposedMarks() {
        // Do we need to initialise pfd with a default???
        this.regionMembership =
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
    }

    public ProposedMarks(Dimensions dimensions) {
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

    public ColoredOverlayCollection getColoredMarks() {
        return overlays;
    }

    public void setColoredMarks(ColoredMarks marks) {
        this.overlays = OverlayCollectionMarkFactory.createColor(marks, regionMembership);
    }

    public void setColoredMarks(ColoredOverlayCollection marks) {
        this.overlays = marks;
    }

    public boolean hasSugestedSliceNum() {
        return hasSuggestedSliceNum;
    }

    public void setSuggestedSliceNum(int suggestedSliceNum) {
        this.hasSuggestedSliceNum = true;
        this.suggestedSliceNum = suggestedSliceNum;
    }

    public Dimensions dimensions() {
        return dimensions;
    }
}
