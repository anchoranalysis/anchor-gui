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
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import lombok.Getter;
import lombok.Setter;

public abstract class ExportTaskRasterGeneratorFromBoundedIndexContainer<T>
        extends ExportTaskRasterGeneratorSequence<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ExportTaskBoundedIndexContainerGeneratorSeries<T> delegate;
    // END BEAN PROPERTIES

    public ExportTaskRasterGeneratorFromBoundedIndexContainer() {
        delegate = new ExportTaskBoundedIndexContainerGeneratorSeries<>();
    }

    public void setBridge(
            FunctionWithException<
                            ExportTaskParams, BoundedIndexContainer<T>, OperationFailedException>
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

    public String getDscrContrib() {
        return delegate.getDscrContrib();
    }

    @Override
    public boolean execute(ExportTaskParams params, ProgressMonitor progressMonitor)
            throws ExportTaskFailedException {

        try {
            return delegate.execute(
                    params,
                    progressMonitor,
                    createGeneratorSequenceWriter(params)
            );
        } catch (CreateException e) {
            throw new ExportTaskFailedException(e);
        }
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
}
