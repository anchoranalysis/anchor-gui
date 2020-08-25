/*-
 * #%L
 * anchor-gui-plot
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

package org.anchoranalysis.gui.plot.visualvm;

import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.MarksWithTotalEnergy;
import org.anchoranalysis.gui.plot.definition.GraphDefinition;
import org.anchoranalysis.gui.videostats.EnergyUpdater;
import org.anchoranalysis.mpp.segment.optscheme.feedback.aggregate.Aggregator;

public class GraphPanelList implements EnergyUpdater, Iterable<GraphPanel> {

    private ArrayList<GraphPanel> delegate = new ArrayList<>();

    public void add(GraphDefinition graphDefinition) {
        delegate.add(new GraphPanel(graphDefinition));
    }

    @Override
    public void updateCurrent(int iter, long timeStamp, MarksWithTotalEnergy current, Aggregator aggregator) {
        for (GraphPanel gp : delegate) {
            gp.updateCurrent(iter, timeStamp, current, aggregator);
        }
    }

    @Override
    public void updateBest(int iter, long timeStamp, MarksWithTotalEnergy best) {
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
