/* (C)2020 */
package org.anchoranalysis.gui.plot.visualvm;

import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.gui.plot.definition.GraphDefinition;
import org.anchoranalysis.gui.videostats.ICfgNRGUpdater;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class GraphPanelList implements ICfgNRGUpdater, Iterable<GraphPanel> {

    private ArrayList<GraphPanel> delegate = new ArrayList<>();

    public void add(GraphDefinition graphDefinition) {
        delegate.add(new GraphPanel(graphDefinition));
    }

    @Override
    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {
        for (GraphPanel gp : delegate) {
            gp.updateCrnt(iter, timeStamp, crnt, agg);
        }
    }

    @Override
    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {
        for (GraphPanel gp : delegate) {
            gp.updateBest(iter, timeStamp, best);
        }
    }

    @Override
    public boolean equals(Object arg0) {
        return delegate.equals(arg0);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public Iterator<GraphPanel> iterator() {
        return delegate.iterator();
    }
}
