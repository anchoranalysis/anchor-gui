/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import java.util.List;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.core.geometry.Point3i;

public interface IReplaceRemove {

    void removeCurrentProposedCfg();

    void replaceCurrentProposedCfg(Cfg cfgCore, ColoredCfg cfgDisplayed, int sliceZ);

    void removeAcceptedMarksAndSelectedPoints(Cfg cfg, List<Point3i> points);
}
