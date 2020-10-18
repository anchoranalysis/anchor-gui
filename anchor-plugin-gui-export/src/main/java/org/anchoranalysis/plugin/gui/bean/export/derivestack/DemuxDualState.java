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

package org.anchoranalysis.plugin.gui.bean.export.derivestack;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorBridge;
import org.anchoranalysis.plugin.gui.export.DualStateWithoutIndex;
import org.anchoranalysis.plugin.gui.export.MappedFrom;

public class DemuxDualState<T> extends DeriveStack<DualStateWithoutIndex<T>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int index = 0;

    @BeanField @Getter @Setter private DeriveStack<T> item;
    // END BEAN PROPERTIES

    private T demux(DualStateWithoutIndex<T> in) {
        return in.getItem(index);
    }

    @Override
    public RasterGenerator<MappedFrom<DualStateWithoutIndex<T>>> createGenerator(
            ExportTaskParams params) throws CreateException {

        RasterGenerator<MappedFrom<T>> generator = item.createGenerator(params);

        return new RasterGeneratorBridge<>(
                generator,
                sourceObject ->
                        new MappedFrom<>(
                                sourceObject.getOriginalIteration(), demux(sourceObject.getObject())));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return true;
    }
}
