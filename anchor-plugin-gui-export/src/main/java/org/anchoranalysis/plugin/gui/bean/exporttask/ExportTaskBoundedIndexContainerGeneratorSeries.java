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

import javax.swing.ProgressMonitor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncremental;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ExportTaskBoundedIndexContainerGeneratorSeries<T>
        extends AnchorBean<ExportTaskBoundedIndexContainerGeneratorSeries<T>> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private int incrementSize = 1000;

    /**
     * We start at the end, and move to the front (should be set with a negative // incrementSize)
     */
    @BeanField @Getter @Setter private boolean startAtEnd = false;

    /** A limit on the number of iterations (not applied if equal to -1) */
    @BeanField @Getter @Setter private int limitIterations = -1;
    // END BEAN PARAMETERS

    @Setter
    private CheckedFunction<ExportTaskParams, BoundedIndexContainer<T>, OperationFailedException>
            containerBridge;

    public boolean execute(
            ExportTaskParams params,
            ProgressMonitor progressMonitor,
            GeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter)
            throws ExportTaskFailedException {
        try {
            return execute(containerBridge.apply(params), progressMonitor, generatorSequenceWriter);
        } catch (OutputWriteFailedException
                | OperationFailedException
                | GetOperationFailedException e) {
            throw new ExportTaskFailedException(e);
        }
    }

    public int getMinProgress(ExportTaskParams params) throws ExportTaskFailedException {
        try {
            return containerBridge.apply(params).getMinimumIndex();
        } catch (OperationFailedException e) {
            throw new ExportTaskFailedException(e);
        }
    }

    public int getMaxProgress(ExportTaskParams params) throws ExportTaskFailedException {
        try {
            return containerBridge.apply(params).getMaximumIndex();
        } catch (OperationFailedException e) {
            throw new ExportTaskFailedException(e);
        }
    }

    public String getDscrContrib() {
        return String.format("incrementSize=%d", this.incrementSize);
    }

    public CheckedFunction<ExportTaskParams, BoundedIndexContainer<T>, OperationFailedException>
            getContainerBridge() {
        return containerBridge;
    }

    private boolean execute(
            BoundedIndexContainer<T> container,
            ProgressMonitor progressMonitor,
            GeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter)
            throws OutputWriteFailedException, GetOperationFailedException {

        int min = container.getMinimumIndex();
        int max = container.getMaximumIndex();

        int numAdd = calculateIncrements(min, max);

        int indexOut = 0;
        IncrementalSequenceType sequenceType = new IncrementalSequenceType(indexOut);
        sequenceType.setIncrementSize(1);

        sequenceType.setStart(min);

        int realNumAdd = numAdd;
        if (limitIterations >= 0) {
            realNumAdd = Math.min(numAdd, limitIterations);
        }

        generatorSequenceWriter.start(sequenceType, realNumAdd);

        if (startAtEnd) {
            for (int i = max; i >= min; i -= incrementSize) {

                // Escape if we reach our match iterations (limitIterations is off if its negative)
                if (indexOut == limitIterations) {
                    break;
                }

                if (progressMonitor.isCanceled()) {
                    return false;
                }

                addToWriter(container, generatorSequenceWriter, i, indexOut);

                progressMonitor.setProgress(indexOut++);
            }
        } else {
            for (int i = min; i <= max; i += incrementSize) {

                // Escape if we reach our match iterations (limitIterations is off if its negative)
                if (indexOut == limitIterations) {
                    break;
                }

                if (progressMonitor.isCanceled()) {
                    return false;
                }

                addToWriter(container, generatorSequenceWriter, i, indexOut);

                progressMonitor.setProgress(indexOut++);
            }
        }
        progressMonitor.setProgress(max);

        generatorSequenceWriter.end();

        return true;
    }

    private int calculateIncrements(int min, int max) {
        double incr = (double) (max - min) / incrementSize;
        return (int) Math.floor(incr) + 1;
    }

    private void addToWriter(
            BoundedIndexContainer<T> cntr,
            GeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter,
            int i,
            int indexOut)
            throws OutputWriteFailedException, GetOperationFailedException {
        int index = cntr.previousEqualIndex(i);
        generatorSequenceWriter.add(new MappedFrom<>(i, cntr.get(index)), String.valueOf(indexOut));
    }
}
