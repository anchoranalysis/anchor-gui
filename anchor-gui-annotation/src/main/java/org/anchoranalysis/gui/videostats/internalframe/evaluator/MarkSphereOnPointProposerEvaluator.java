/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.awt.Color;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.conic.Sphere;
import org.anchoranalysis.mpp.overlay.OverlayMark;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.spatial.point.Point3d;

@RequiredArgsConstructor
public class MarkSphereOnPointProposerEvaluator implements ProposalOperationCreator {

    // START REQUIRED ARGUMENTS
    private final Dimensions dimensions;
    // END REQUIRED ARGUMENTS

    private RGBColor colorMark = new RGBColor(Color.YELLOW);

    private static RegionMembershipWithFlags regionMembership =
            RegionMapSingleton.instance()
                    .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    @Override
    public ProposalOperation create(
            MarkCollection marks,
            final Point3d position,
            ProposerContext context,
            MarkWithIdentifierFactory markFactory)
            throws OperationFailedException {

        return errorNode -> {
            ProposedMarks proposedMarks = new ProposedMarks(dimensions);
            proposedMarks.setSuccess(true);

            Sphere markSphere = new Sphere();
            markSphere.setRadius(1);
            markSphere.setPosition(position);

            proposedMarks.getMarksCore().add(markSphere);
            proposedMarks.getMarksToRedraw().add(markSphere);
            proposedMarks
                    .getColoredMarks()
                    .add(new OverlayMark(markSphere, regionMembership), colorMark);
            proposedMarks.setSuggestedSliceNum((int) position.z());

            return proposedMarks;
        };
    }
}
