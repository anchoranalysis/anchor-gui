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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.plot.PlotInstance;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

public class ClickablePlotInstance {

    private PlotInstance graphInstance;

    private GraphPanelMouseClickListener panelMouseClickListener;

    private Optional<IPropertyValueReceivable<Integer>> selectFrameReceivable = Optional.empty();
    private Optional<IPropertyValueSendable<Integer>> selectFrameSendable = Optional.empty();

    ClickablePlotInstance(PlotInstance graphInstance) {
        this.graphInstance = graphInstance;
    }

    public Optional<IPropertyValueReceivable<Integer>> getSelectFrameReceivable() {
        return selectFrameReceivable;
    }

    public void setSelectFrameReceivable(IPropertyValueReceivable<Integer> selectFrameReceivable) {
        this.selectFrameReceivable = Optional.of(selectFrameReceivable);
    }

    public Optional<IPropertyValueSendable<Integer>> getSelectFrameSendable() {
        return selectFrameSendable;
    }

    public void setSelectFrameSendable(IPropertyValueSendable<Integer> selectFrameSendable) {
        this.selectFrameSendable = Optional.of(selectFrameSendable);
    }

    private static class XAxisClickListener
            implements GraphPanelMouseClickListener, IPropertyValueReceivable<Integer> {

        private EventListenerList eventListenerList = new EventListenerList();

        private int minIndex;
        private int maxIndex;

        public XAxisClickListener(int minIndex, int maxIndex) {
            super();
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        @Override
        public void addPropertyValueChangeListener(
                PropertyValueChangeListener<Integer> changeListener) {
            eventListenerList.add(PropertyValueChangeListener.class, changeListener);
        }

        @Override
        public void removePropertyValueChangeListener(
                PropertyValueChangeListener<Integer> changeListener) {
            eventListenerList.remove(PropertyValueChangeListener.class, changeListener);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void chartMouseClicked(ChartPanel chartPanel, ChartMouseEvent event) {
            Point2D p = event.getTrigger().getPoint();
            Rectangle2D plotArea = chartPanel.getScreenDataArea();

            JFreeChart chart = (JFreeChart) event.getSource();

            XYPlot plot = (XYPlot) chart.getPlot(); // your plot
            double chartX =
                    plot.getDomainAxis()
                            .java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
            // double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea,
            // plot.getRangeAxisEdge());

            if (chartX >= minIndex && chartX <= maxIndex) {

                for (PropertyValueChangeListener<Integer> l :
                        eventListenerList.getListeners(PropertyValueChangeListener.class)) {
                    l.propertyValueChanged(
                            new PropertyValueChangeEvent<>(this, (int) chartX, false));
                }
            }
        }
    }

    public void addXAxisIndexListener(int minIndex, int maxIndex) {

        final XAxisClickListener clickListener = new XAxisClickListener(minIndex, maxIndex);
        setPanelMouseClickListener(clickListener);
        setSelectFrameSendable(
                (Integer frameIndex, boolean adjusting) ->
                        graphInstance.getChart().getXYPlot().setDomainCrosshairValue(frameIndex));
        setSelectFrameReceivable(clickListener);
    }

    public GraphPanelMouseClickListener getPanelMouseClickListener() {
        return panelMouseClickListener;
    }

    public void setPanelMouseClickListener(GraphPanelMouseClickListener panelMouseClickListener) {
        this.panelMouseClickListener = panelMouseClickListener;
    }

    public JFreeChart getChart() {
        return graphInstance.getChart();
    }

    public PlotInstance getGraphInstance() {
        return graphInstance;
    }
}
