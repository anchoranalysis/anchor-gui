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

package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindPoints {

    private static final int DISTANCE_THRESHOLD = 5;

    private static final int DISTANCE_THRESHOLD_SQUARED = DISTANCE_THRESHOLD * DISTANCE_THRESHOLD;

    public static Cfg findMarksContainingPoint(
            Cfg cfg, Point3d point, RegionMap regionMap, int regionID) {

        Cfg cfgOut = new Cfg();

        RegionMembershipWithFlags rm = regionMap.membershipWithFlagsForIndex(regionID);

        // Find marks that contain the point x, y
        for (Mark m : cfg) {
            byte membership = m.evalPointInside(point);
            if (rm.isMemberFlag(membership)) {
                cfgOut.add(m);
            }
        }
        return cfgOut;
    }

    public static List<Point3i> findSelectedPointsNear(
            Point3d point, IQuerySelectedPoints selectedPoints) {
        return findSelectedPointsNear(PointConverter.intFromDouble(point), selectedPoints);
    }

    private static List<Point3i> findSelectedPointsNear(
            Point3i pointNear, IQuerySelectedPoints selectedPoints) {

        List<Point3i> listPoints = selectedPoints.selectedPointsAsIntegers();

        // Find marks that contain the point x, y
        return FunctionalList.mapToListOptional(
                listPoints,
                point -> {
                    int distanceSquared = distanceFromPoints(point, pointNear);
                    return OptionalUtilities.createFromFlag(
                            distanceSquared < DISTANCE_THRESHOLD_SQUARED, point);
                });
    }

    private static int distanceFromPoints(Point3i p1, Point3i p2) {
        int px = p1.getX() - p2.getX();
        int py = p1.getY() - p2.getY();
        int pz = p1.getZ() - p2.getZ();

        return (px * px) + (py * py) + (pz * pz);
    }
}
