/*-
 * #%L
 * anchor-mpp-plot
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

package org.anchoranalysis.gui.plot.bean;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.gui.plot.EnergyGraphItem;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plot.bean.Plot;
import org.anchoranalysis.plot.bean.colorscheme.PlotColorScheme;
import org.anchoranalysis.plot.index.BarPlot;

public class BarPlotEnergyBreakdown extends Plot<EnergyGraphItem> {

    private BarPlot<EnergyGraphItem> delegate;

    public BarPlotEnergyBreakdown() throws InitException {

        delegate =
                new BarPlot<>(
                        getTitle(),
                        new String[] {"Energy Total"},
                        (EnergyGraphItem item, int seriesIndex) -> item.getObjectID(),
                        (EnergyGraphItem item, int seriesIndex) -> item.getEnergy(),
                        (EnergyGraphItem item, int seriesIndex) -> item.getPaint(),
                        false);
        delegate.getLabels().setX("Mark");
        delegate.getLabels().setY("Energy Coefficient");
    }

    @Override
    public PlotInstance create(
            Iterator<EnergyGraphItem> itr,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        return delegate.createWithRangeLimits(itr, rangeLimits);
    }

    @Override
    public String getTitle() {
        return "Energy Breakdown";
    }

    @Override
    public boolean isItemIncluded(EnergyGraphItem item) {
        return true;
    }

    // START BEAN PROPERTIES
    public boolean isShowDomainAxis() {
        return delegate.isShowDomainAxis();
    }

    public void setShowDomainAxis(boolean showDomainAxis) {
        delegate.setShowDomainAxis(showDomainAxis);
    }
    // END BEAN PROPERTIES

    @Override
    public String getShortTitle() {
        return getTitle();
    }

    public PlotColorScheme getGraphColorScheme() {
        return delegate.getGraphColorScheme();
    }

    public void setGraphColorScheme(PlotColorScheme graphColorScheme) {
        delegate.setGraphColorScheme(graphColorScheme);
    }
}
