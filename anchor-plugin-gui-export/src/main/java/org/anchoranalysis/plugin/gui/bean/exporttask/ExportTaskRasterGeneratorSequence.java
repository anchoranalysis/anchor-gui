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

package org.anchoranalysis.plugin.gui.bean.exporttask;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.io.generator.bean.sequence.factory.GeneratorSequenceFactory;
import org.anchoranalysis.io.generator.bean.sequence.factory.SubfolderGeneratorSequenceFactory;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncremental;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;

public abstract class ExportTaskRasterGeneratorSequence<T> extends ExportTaskBean {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter
    private GeneratorSequenceFactory sequenceFactory = new SubfolderGeneratorSequenceFactory();

    @BeanField @Getter @Setter private CreateRasterGenerator<T> createRasterGenerator;

    @BeanField @Setter private String outputName = "defaultOutputName";
    // END BEAN PARAMETERS

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return createRasterGenerator.hasNecessaryParams(params);
    }

    protected GeneratorSequenceNonIncremental<MappedFrom<T>> createGeneratorSequenceWriter(
            ExportTaskParams params) throws CreateException {

        return getSequenceFactory()
                .createGeneratorSequenceNonIncremental(
                        params.getOutputManager(),
                        getOutputName(),
                        getCreateRasterGenerator().createGenerator(params));
    }

    @Override
    public String getOutputName() {
        return outputName;
    }
}
