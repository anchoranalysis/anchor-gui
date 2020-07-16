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

public class GraphDefinitionLineIterVsNRG
        extends GraphDefinition<GraphDefinitionLineIterVsNRG.Item> {

    // START BEAN PROPERITES
    @BeanField private GraphColorScheme graphColorScheme = new GraphColorScheme();

    @BeanField private int minMaxIgnoreBeforeIndex = 0;
    // END BEAN PROPERTIES

    // Item
    public static class Item implements IIndexGetter {
        private int iter;
        private double nrg;

        public Item() {}

        public Item(int iter, double nrg) {
            super();
            this.iter = iter;
            this.nrg = nrg;
        }

        public int getIter() {
            return iter;
        }

        public void setIter(int iter) {
            this.iter = iter;
        }

        public double getNrg() {
            return nrg;
        }

        public void setNrg(double nrg) {
            this.nrg = nrg;
        }

        @Override
        public int getIndex() {
            return iter;
        }
    }

    public GraphDefinitionLineIterVsNRG() {}

    @Override
    public GraphInstance create(
            Iterator<GraphDefinitionLineIterVsNRG.Item> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        LinePlot<GraphDefinitionLineIterVsNRG.Item> delegate =
                new LinePlot<>(
                        getTitle(),
                        new String[] {"NRG"},
                        (Item item, int yIndex) -> raiseNrgToLog(item.getNrg()));
        delegate.setMinMaxIgnoreBeforeIndex(minMaxIgnoreBeforeIndex);
        delegate.getLabels().setXY("Iteration", "NRG");
        delegate.setYAxisMargins(1);
        delegate.setGraphColorScheme(graphColorScheme);
        return delegate.create(items, domainLimits, rangeLimits);
    }

    private static double raiseNrgToLog(double in) {
        double raised = -1 * Math.log(in);

        return replaceInfiniteWithMax(raised);
    }

    private static double replaceInfiniteWithMax(double in) {
        // Account for infinite values
        if (Double.isInfinite(in)) {
            return Double.MAX_VALUE;
        } else {
            return in;
        }
    }

    @Override
    public String getTitle() {
        return "NRG Graph";
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

    public int getMinMaxIgnoreBeforeIndex() {
        return minMaxIgnoreBeforeIndex;
    }

    public void setMinMaxIgnoreBeforeIndex(int minMaxIgnoreBeforeIndex) {
        this.minMaxIgnoreBeforeIndex = minMaxIgnoreBeforeIndex;
    }
}
