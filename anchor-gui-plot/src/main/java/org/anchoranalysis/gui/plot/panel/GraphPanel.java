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

package org.anchoranalysis.gui.plot.panel;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;

public class GraphPanel {

    private final ChartPanel chartPanel;

    private EventListenerList eventListenerList = new EventListenerList();

    public GraphPanel(ClickablePlotInstance graphInstance) {

        // we put the chart into a panel
        this.chartPanel = new ChartPanel(graphInstance.getChart());
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 470));
        chartPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        chartPanel.setMouseZoomable(false);
        chartPanel.setMouseWheelEnabled(true);

        if (graphInstance.getPanelMouseClickListener() != null) {
            eventListenerList.add(
                    GraphPanelMouseClickListener.class, graphInstance.getPanelMouseClickListener());
        }

        // Passes ChartMoustEvent to our PanelMouseClickListeners
        chartPanel.addChartMouseListener(
                new ChartMouseListener() {

                    @Override
                    public void chartMouseMoved(ChartMouseEvent arg0) {}

                    @Override
                    public void chartMouseClicked(ChartMouseEvent arg0) {

                        for (GraphPanelMouseClickListener l :
                                eventListenerList.getListeners(
                                        GraphPanelMouseClickListener.class)) {
                            l.chartMouseClicked(chartPanel, arg0);
                        }
                    }
                });
    }

    public void updateGraph(ClickablePlotInstance graphInstance) {

        if (graphInstance != null) {
            chartPanel.setChart(graphInstance.getChart());
        } else {
            chartPanel.setChart(null);
        }
    }

    public JPanel getPanel() {
        return chartPanel;
    }
}
