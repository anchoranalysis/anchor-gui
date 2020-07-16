/* (C)2020 */
package org.anchoranalysis.gui.plot.definition.line;

import java.util.Iterator;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @BeanField @Getter @Setter private GraphColorScheme graphColorScheme = new GraphColorScheme();
    // END BEAN PROPERTIES

    // Item
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item implements IIndexGetter {
        private int iter;

        @Getter private double cfgSize;

        @Override
        public int getIndex() {
            return iter;
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
}
