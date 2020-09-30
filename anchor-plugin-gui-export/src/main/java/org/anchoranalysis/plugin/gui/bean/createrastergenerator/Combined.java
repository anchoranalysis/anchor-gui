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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.image.bean.spatial.arrange.ArrangeStackBean;
import org.anchoranalysis.image.io.bean.generator.CombineRasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class Combined<T> extends GeneratorFactory<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<GeneratorFactory<T>> createRasterGeneratorList;

    @BeanField @Getter @Setter private ArrangeStackBean arrange;
    // END BEAN PROPERTIES

    @Override
    public RasterGenerator<MappedFrom<T>> createGenerator(ExportTaskParams params)
            throws CreateException {

        CombineRasterGenerator<MappedFrom<T>> combineGenerator = new CombineRasterGenerator<>();
        combineGenerator.setArrange(arrange);

        for (GeneratorFactory<T> creator : createRasterGeneratorList) {
            combineGenerator.add(creator.createGenerator(params));
        }

        return combineGenerator.createGenerator();
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {

        for (GeneratorFactory<T> creator : createRasterGeneratorList) {

            if (!creator.hasNecessaryParams(params)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String describeBean() {
        return getBeanName();
    }
}
