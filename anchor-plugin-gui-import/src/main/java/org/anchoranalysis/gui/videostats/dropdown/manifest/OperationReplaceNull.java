/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.dropdown.manifest;

import org.anchoranalysis.core.progress.CallableWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;

/**
 * Performs the first operation. If it returns a null, performs the second-operation
 *
 * @param <T> return-type of operation
 * @param <E> exception-type if something goes wrong during operation
 */
class OperationReplaceNull<T, E extends Exception> implements CallableWithProgressReporter<T, E> {

    private CallableWithProgressReporter<T, E> opFirst;
    private CallableWithProgressReporter<T, E> opSecond;

    public OperationReplaceNull(
            CallableWithProgressReporter<T, E> opFirst,
            CallableWithProgressReporter<T, E> opSecond) {
        super();
        this.opFirst = opFirst;
        this.opSecond = opSecond;
        assert (opFirst != null);
        assert (opSecond != null);
    }

    @Override
    public T call(ProgressReporter progressReporter) throws E {

        T first = opFirst.call(progressReporter);

        if (first == null) {
            return opSecond.call(ProgressReporterNull.get());
        }

        return first;
    }
}
