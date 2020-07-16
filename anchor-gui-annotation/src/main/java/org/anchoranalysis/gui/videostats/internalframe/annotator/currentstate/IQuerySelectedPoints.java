/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import java.util.List;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;

public interface IQuerySelectedPoints {

    boolean hasSelectedPoints();

    List<Point3i> selectedPointsAsIntegers();

    List<Point3f> selectedPointsAsFloats();
}
