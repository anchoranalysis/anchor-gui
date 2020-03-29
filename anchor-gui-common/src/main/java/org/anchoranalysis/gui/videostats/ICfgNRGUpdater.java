package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public interface ICfgNRGUpdater {

	public void updateCrnt( int iter, long timeStamp, CfgWithNrgTotal crnt, Aggregator agg );
   	    
    public void updateBest( int iter, long timeStamp, CfgWithNrgTotal best );
}
