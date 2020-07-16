/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public interface ICfgNRGUpdater {

    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg);

    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best);
}
