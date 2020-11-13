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

import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;
import com.google.common.base.Preconditions;

public class ProgressInteractiveWorker implements Progress {

    private int min;
    private int max;
    private InteractiveWorker<?, ?> worker;

    private int range;

    public ProgressInteractiveWorker(InteractiveWorker<?, ?> worker) {
        this(worker, 0, 1);
    }

    public ProgressInteractiveWorker(InteractiveWorker<?, ?> worker, int min, int max) {
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
        // NOTHING TO DO
    }

    @Override
    public void update(int value) {
        Preconditions.checkArgument(value <= max);
        int valRange = value - min;
        double progress = (((double) valRange) / range) * 100;
        worker.updateProgress((int) Math.round(progress));
    }

    @Override
    public void close() {
        worker.updateProgress(100);
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
