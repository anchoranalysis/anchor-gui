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

package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.overlap.OverlapUtilities;

// Always uses the first region
class OverlapChecker {

    // START PARAMETERS for overlap check
    private double largeOverlapThreshold = 0.3;
    // END PARAMETERS

    private EnergyStackWithoutParams energyStack;
    private ToolErrorReporter errorReporter;
    private RegionMap regionMap;

    public OverlapChecker(
            Dimensions dimensions, RegionMap regionMap, ToolErrorReporter errorReporter) {
        super();
        this.energyStack = new EnergyStackWithoutParams(dimensions);
        this.errorReporter = errorReporter;
        this.regionMap = regionMap;
    }

    public static double calculateOverlapRatio(
            VoxelizedMarkMemo obj1, VoxelizedMarkMemo obj2, double overlap, int regionID)
            throws FeatureCalculationException {
        return overlap / calculateMinVolume(obj1, obj2, regionID);
    }

    // We look for larger overlap to warn the user
    public boolean hasLargeOverlap(MarkCollection proposed, MarkCollection existing) {

        for (Mark prop : proposed) {

            VoxelizedMarkMemo pmProp = new VoxelizedMarkMemo(prop, energyStack, regionMap);

            for (Mark exst : existing) {
                VoxelizedMarkMemo pmExst = new VoxelizedMarkMemo(exst, energyStack, regionMap);

                try {
                    if (boundingBoxIntersectionExists(pmProp, pmExst)
                            && hasLargeOverlap(pmProp, pmExst)) {
                        return true;
                    }
                } catch (OperationFailedException e) {
                    errorReporter.showError(
                            OverlapChecker.class, "Cannot calculate overlap", e.toString());
                }
            }
        }

        return false;
    }

    private boolean hasLargeOverlap(VoxelizedMarkMemo pmProp1, VoxelizedMarkMemo pmProp2)
            throws OperationFailedException {
        try {
            double overlap = OverlapUtilities.overlapWith(pmProp1, pmProp2, 0);
            double overlapRatio = calculateOverlapRatio(pmProp1, pmProp2, overlap, 0);
            return (overlapRatio > largeOverlapThreshold);
        } catch (FeatureCalculationException e) {
            throw new OperationFailedException(e);
        }
    }

    private static boolean boundingBoxIntersectionExists(
            VoxelizedMarkMemo pmProp, VoxelizedMarkMemo pmExst) {
        return pmProp.voxelized()
                .boundingBox()
                .intersection()
                .existsWith(pmExst.voxelized().boundingBox());
    }

    private static double calculateMinVolume(
            VoxelizedMarkMemo obj1, VoxelizedMarkMemo obj2, int regionID) {
        return Math.min(obj1.getMark().volume(regionID), obj2.getMark().volume(regionID));
    }
}
