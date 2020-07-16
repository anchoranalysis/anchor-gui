/* (C)2020 */
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
                p -> {
                    int distanceSquared = distanceFromPoints(p, pointNear);
                    return OptionalUtilities.createFromFlag(
                            distanceSquared < DISTANCE_THRESHOLD_SQUARED, () -> p);
                });
    }

    private static int distanceFromPoints(Point3i p1, Point3i p2) {
        int px = p1.getX() - p2.getX();
        int py = p1.getY() - p2.getY();
        int pz = p1.getZ() - p2.getZ();

        return (px * px) + (py * py) + (pz * pz);
    }
}
