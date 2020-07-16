/* (C)2020 */
package org.anchoranalysis.gui.plot.panel;

import java.util.EventListener;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;

interface GraphPanelMouseClickListener extends EventListener {

    void chartMouseClicked(ChartPanel chartPanel, ChartMouseEvent event);
}
