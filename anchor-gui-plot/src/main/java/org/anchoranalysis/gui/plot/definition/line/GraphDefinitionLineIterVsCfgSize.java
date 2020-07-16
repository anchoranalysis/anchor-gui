/* (C)2020 */
package org.anchoranalysis.gui.plot.definition.line;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.IIndexGetter;

public class GraphDefinitionLineIterVsCfgSize
        extends GraphDefinition<GraphDefinitionLineIterVsCfgSize.Item> {

    // START BEAN PROPERITES
    @BeanField private GraphColorScheme graphColorScheme = new GraphColorScheme();
    // END BEAN PROPERTIES

    // Item
    public static class Item implements IIndexGetter {
        private int iter;
        private double cfgSize;

        public Item() {}

        public Item(int iter, double cfgSize) {
            super();
            this.iter = iter;
            this.cfgSize = cfgSize;
        }

        public int getIter() {
            return iter;
        }

        public void setIter(int iter) {
            this.iter = iter;
        }

        @Override
        public int getIndex() {
            return iter;
        }

        public double getCfgSize() {
            return cfgSize;
        }

        public void setCfgSize(double cfgSize) {
            this.cfgSize = cfgSize;
        }
    }

    @Override
    public GraphInstance create(
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

    public GraphColorScheme getGraphColorScheme() {
        return graphColorScheme;
    }

    public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
        this.graphColorScheme = graphColorScheme;
    }
}
