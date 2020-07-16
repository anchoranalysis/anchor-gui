/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.manifest;

import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;

/**
 * Performs the first operation. If it returns a null, performs the second-operation
 *
 * @param <T> return-type of operation
 * @param <E> exception-type if something goes wrong during operation
 */
class OperationReplaceNull<T, E extends Exception> implements OperationWithProgressReporter<T, E> {

    private OperationWithProgressReporter<T, E> opFirst;
    private OperationWithProgressReporter<T, E> opSecond;

    public OperationReplaceNull(
            OperationWithProgressReporter<T, E> opFirst,
            OperationWithProgressReporter<T, E> opSecond) {
        super();
        this.opFirst = opFirst;
        this.opSecond = opSecond;
        assert (opFirst != null);
        assert (opSecond != null);
    }

    @Override
    public T doOperation(ProgressReporter progressReporter) throws E {

        T first = opFirst.doOperation(progressReporter);

        if (first == null) {
            return opSecond.doOperation(ProgressReporterNull.get());
        }

        return first;
    }
}
