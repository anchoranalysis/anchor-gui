/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import javax.swing.ProgressMonitor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;

public abstract class ExportTaskRasterGeneratorFromBoundedIndexContainer<T>
        extends ExportTaskRasterGeneratorSequence<T> {

    // START BEAN PROPERTIES
    @BeanField private ExportTaskBoundedIndexContainerGeneratorSeries<T> delegate;
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
                    createGeneratorSequenceWriter(params),
                    params.getOutputManager(),
                    getOutputName());
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

    public ExportTaskBoundedIndexContainerGeneratorSeries<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(ExportTaskBoundedIndexContainerGeneratorSeries<T> delegate) {
        this.delegate = delegate;
    }
}
