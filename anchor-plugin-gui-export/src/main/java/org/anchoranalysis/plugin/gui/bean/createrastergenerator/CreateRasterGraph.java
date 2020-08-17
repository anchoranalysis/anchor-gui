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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.plot.PlotInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public abstract class CreateRasterGraph<T, S> extends CreateRasterGenerator<S> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private GraphDefinition<T> graphDefinition;

    @BeanField @Getter @Setter private int width = 1024;

    @BeanField @Getter @Setter private int height = 768;
    // END BEAN PARAMETERS

    protected IterableObjectGenerator<PlotInstance, Stack> createGraphInstanceGenerator() {
        return new GraphInstanceGenerator(width, height);
    }

    @Override
    public abstract IterableObjectGenerator<MappedFrom<S>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException;

    @Override
    public abstract boolean hasNecessaryParams(ExportTaskParams params);

    @Override
    public String descriptionBean() {
        return String.format(
                "graph=%s, width=%d, height=%d", graphDefinition.getTitle(), width, height);
    }
}
