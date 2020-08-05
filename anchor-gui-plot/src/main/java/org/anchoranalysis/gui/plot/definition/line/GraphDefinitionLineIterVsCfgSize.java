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

package org.anchoranalysis.gui.plot.definition.line;

import java.util.Iterator;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.PlotInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.IndexGetter;

public class GraphDefinitionLineIterVsCfgSize
        extends GraphDefinition<GraphDefinitionLineIterVsCfgSize.Item> {

    // START BEAN PROPERITES
    @BeanField @Getter @Setter private GraphColorScheme graphColorScheme = new GraphColorScheme();
    // END BEAN PROPERTIES

    // Item
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item implements IndexGetter {
        private int iter;

        @Getter private double cfgSize;

        @Override
        public int getIndex() {
            return iter;
        }
    }

    @Override
    public PlotInstance create(
            Iterator<GraphDefinitionLineIterVsCfgSize.Item> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        LinePlot<GraphDefinitionLineIterVsCfgSize.Item> delegate =
                new LinePlot<>(
                        getTitle(),
                        new String[] {"Cfg Size"},
                        (Item item, int yIndex) -> item.getCfgSize());
        delegate.getLabels().setXY("Iteration", "Number of Marks");
        delegate.setGraphColorScheme(graphColorScheme);
        return delegate.create(items, domainLimits, rangeLimits);
    }

    @Override
    public String getTitle() {
        return "Configuration Size";
    }

    @Override
    public boolean isItemAccepted(Item item) {
        return true;
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }
}
