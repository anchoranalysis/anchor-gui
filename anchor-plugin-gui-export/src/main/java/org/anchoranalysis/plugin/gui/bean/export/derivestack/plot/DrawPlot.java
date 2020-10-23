/*-
 * #%L
 * anchor-plugin-gui-export
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

package org.anchoranalysis.plugin.gui.bean.export.derivestack.plot;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.image.bean.spatial.SizeXY;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.plot.bean.Plot;
import org.anchoranalysis.plugin.gui.bean.export.derivestack.DeriveStack;
import org.anchoranalysis.plugin.gui.export.MappedFrom;

public abstract class DrawPlot<T, S> extends DeriveStack<S> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private Plot<T> graphDefinition;

    @BeanField @Getter @Setter private SizeXY size = new SizeXY(1024, 768);
    // END BEAN PARAMETERS

    protected PlotGenerator createPlotGenerator() {
        return new PlotGenerator(size);
    }

    @Override
    public abstract RasterGenerator<MappedFrom<S>> createGenerator(ExportTaskParams params)
            throws CreateException;

    @Override
    public abstract boolean hasNecessaryParams(ExportTaskParams params);

    @Override
    public String describeBean() {
        return String.format("graph=%s, size=%s", graphDefinition.getTitle(), size);
    }
}
