/* (C)2020 */
package org.anchoranalysis.gui.plot.panel;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;

public class GraphPanel {

    private final ChartPanel chartPanel;

    private EventListenerList eventListenerList = new EventListenerList();

    public GraphPanel(ClickableGraphInstance graphInstance) {

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

        // Pases ChartMoustEvent to our PanelMouseClickListeners
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

    public void updateGraph(ClickableGraphInstance graphInstance) {

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
