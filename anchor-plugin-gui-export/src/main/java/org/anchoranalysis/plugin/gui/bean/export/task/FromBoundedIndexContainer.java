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

package org.anchoranalysis.plugin.gui.bean.export.task;

import javax.swing.ProgressMonitor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.gui.export.bean.ExportTaskFailedException;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.plugin.gui.export.CreateOutputSequence;
import org.anchoranalysis.plugin.gui.export.MappedFrom;

public abstract class FromBoundedIndexContainer<T> extends ExportStackSequence<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FromBoundedIndexContainerGeneratorSeries<T> delegate;
    // END BEAN PROPERTIES

    public FromBoundedIndexContainer() {
        delegate = new FromBoundedIndexContainerGeneratorSeries<>();
    }

    public void setBridge(
            CheckedFunction<ExportTaskParams, BoundedIndexContainer<T>, OperationFailedException>
                    containerBridge) {
        delegate.setContainerBridge(containerBridge);
    }

    @Override
    public int getMinProgress(ExportTaskParams params) throws ExportTaskFailedException {
        return delegate.getMinProgress(params);
    }

    @Override
    public int getMaxProgress(ExportTaskParams params) throws ExportTaskFailedException {
        return delegate.getMaxProgress(params);
    }

    public int getIncrementSize() {
        return delegate.getIncrementSize();
    }

    public void setIncrementSize(int incrementSize) {
        delegate.setIncrementSize(incrementSize);
    }

    @Override
    public boolean execute(ExportTaskParams params, ProgressMonitor progressMonitor)
            throws ExportTaskFailedException {
        return delegate.execute(params, progressMonitor, outputSequenceCreator(params));
    }

    public boolean isStartAtEnd() {
        return delegate.isStartAtEnd();
    }

    public void setStartAtEnd(boolean startAtEnd) {
        delegate.setStartAtEnd(startAtEnd);
    }

    public int getLimitIterations() {
        return delegate.getLimitIterations();
    }

    public void setLimitIterations(int limitIterations) {
        delegate.setLimitIterations(limitIterations);
    }

    private CreateOutputSequence<T> outputSequenceCreator(ExportTaskParams params) {
        return startIndex ->
                new OutputSequenceFactory<>(
                                createGenerator(params),
                                params.getInputOutputContext().getOutputter().getChecked())
                        .incrementingIntegers(
                                new OutputPatternIntegerSuffix(getOutputName(), false),
                                startIndex,
                                1);
    }

    private Generator<MappedFrom<T>> createGenerator(ExportTaskParams params)
            throws OutputWriteFailedException {
        try {
            return getStack().createGenerator(params);
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
