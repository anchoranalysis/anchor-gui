/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.instantstate;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.IndexableOverlays;
import org.anchoranalysis.overlay.collection.OverlayCollection;

/**
 * Bridges {@link IndexableMarksWithEnergy} to {@link IndexableOverlays}
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class EnergyInstantStateBridge
        implements CheckedFunction<
                IndexableMarksWithEnergy, IndexableOverlays, OperationFailedException> {

    private RegionMembershipWithFlags regionMembership;

    @Override
    public IndexableOverlays apply(IndexableMarksWithEnergy sourceObject)
            throws OperationFailedException {

        if (sourceObject == null) {
            throw new OperationFailedException("The sourceObject is null. Invalid index");
        }

        if (sourceObject.getMarks() == null) {
            return new IndexableOverlays(sourceObject.getIndex(), new OverlayCollection());
        }

        MarkCollection marks = sourceObject.getMarks().getMarks();

        if (marks == null) {
            marks = new MarkCollection();
        }

        OverlayCollection overlays =
                OverlayCollectionMarkFactory.createWithoutColor(marks, regionMembership);

        return new IndexableOverlays(sourceObject.getIndex(), overlays);
    }
}
