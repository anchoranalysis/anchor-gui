/* (C)2020 */
package org.anchoranalysis.gui.plot.panel;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.error.CreateException;
import org.jfree.data.general.Dataset;

public class ClickableGraphFactory {

    public static <T> ClickableGraphInstance create(
            GraphDefinition<T> definition,
            Iterator<T> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        GraphInstance instance = definition.create(items, domainLimits, rangeLimits);
        return createWithXAxisIndex(instance, domainLimits);
    }

    private static <T, S extends Dataset> ClickableGraphInstance createWithXAxisIndex(
            GraphInstance graphInstance, Optional<AxisLimits> domainLimits) throws CreateException {

        ClickableGraphInstance gl = new ClickableGraphInstance(graphInstance);

        if (domainLimits.isPresent()) {
            gl.addXAxisIndexListener(minAxis(domainLimits.get()), maxAxis(domainLimits.get()));
        }
        return gl;
    }

    private static int minAxis(AxisLimits domainLimits) {
        return (int) domainLimits.getAxisMin();
    }

    private static int maxAxis(AxisLimits domainLimits) {
        return (int) domainLimits.getAxisMax();
    }
}
