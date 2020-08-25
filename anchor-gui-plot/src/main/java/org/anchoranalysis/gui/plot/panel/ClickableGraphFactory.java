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

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plot.bean.Plot;
import org.jfree.data.general.Dataset;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ClickableGraphFactory {

    public static <T> ClickableGraphInstance create(
            Plot<T> definition,
            Iterator<T> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        PlotInstance instance = definition.create(items, domainLimits, rangeLimits);
        return createWithXAxisIndex(instance, domainLimits);
    }

    private static <T, S extends Dataset> ClickableGraphInstance createWithXAxisIndex(
            PlotInstance graphInstance, Optional<AxisLimits> domainLimits) throws CreateException {

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
