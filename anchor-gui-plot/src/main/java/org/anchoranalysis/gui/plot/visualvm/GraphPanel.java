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

import com.sun.tools.visualvm.charts.ChartFactory; // NOSONAR
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.TimeZone;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.gui.plot.definition.GraphDefinition;
import org.anchoranalysis.gui.videostats.ICfgNRGUpdater;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class GraphPanel extends JPanel implements ICfgNRGUpdater {

    private static final long serialVersionUID = -2103164378667275813L;

    private SimpleXYChartSupport support;

    private GraphDefinition graphDefinition = null;

    private long timeZoneOffset;

    // We create an internalframe for every JPanel, this is WEIRD, we should switch to containment
    private JInternalFrame containingFrame;

    public GraphPanel(GraphDefinition graphDefinition) {

        support = ChartFactory.createSimpleXYChart(graphDefinition.descriptor());
        setLayout(new BorderLayout());
        add(support.getChart(), BorderLayout.CENTER);
        this.graphDefinition = graphDefinition;

        this.timeZoneOffset = TimeZone.getDefault().getRawOffset();

        this.containingFrame = createInternalFrame(this);
    }

    public void updateGraph(int iter, long timeStamp) {
        support.addValues(timeStamp - timeZoneOffset, graphDefinition.valueArr(iter, timeStamp));

        String[] details =
                graphDefinition.detailsArr(iter, timeStamp, this.timeZoneOffset, support);
        support.updateDetails(details);
    }

    @Override
    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {

        graphDefinition.updateCrnt(iter, timeStamp, crnt, agg);
        updateGraph(iter, timeStamp);
    }

    @Override
    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {
        graphDefinition.updateBest(iter, timeStamp, best);
    }

    public String getTitle() {
        return graphDefinition.title();
    }

    private static JInternalFrame createInternalFrame(GraphPanel container) {
        return createInternalFrame(container, container.getTitle());
    }

    private static JInternalFrame createInternalFrame(Container container, String title) {

        JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
        internalFrame.setContentPane(container);
        internalFrame.setSize(600, 480);
        internalFrame.setPreferredSize(new Dimension(600, 480));
        internalFrame.setLocation(100, 100);
        internalFrame.setVisible(true);
        return internalFrame;
    }

    public JInternalFrame getContainingFrame() {
        return containingFrame;
    }
}
