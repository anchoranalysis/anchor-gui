/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.progressreporter;

import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

public class ProgressReporterInteractiveWorker implements ProgressReporter {

    private int min;
    private int max;
    private InteractiveWorker<?, ?> worker;

    private int range;

    public ProgressReporterInteractiveWorker(InteractiveWorker<?, ?> worker) {
        this(worker, 0, 1);
    }

    public ProgressReporterInteractiveWorker(InteractiveWorker<?, ?> worker, int min, int max) {
        super();
        this.worker = worker;
        this.min = min;
        this.max = max;
        updateRange();
    }

    private void updateRange() {
        this.range = max - min;
    }

    @Override
    public void open() {
        // System.out.println("Start");
    }

    @Override
    public void update(int val) {
        assert (val <= max);

        int valRec = val - min;
        double progress = (((double) valRec) / range) * 100;
        worker.setProgressExt((int) Math.round(progress));

        // System.out.printf("Update %d of %d\n", val, max);
    }

    @Override
    public void close() {
        worker.setProgressExt(100);
        // System.out.println("End");
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public void setMin(int min) {
        this.min = min;
        updateRange();
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
        updateRange();
    }
}
