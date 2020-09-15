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

package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.DisplayUpdateRememberStack;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedDisplayUpdateConsumer;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedProducer;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.BackgroundSetter;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.image.io.generator.raster.DisplayStackGenerator;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;

public class ThreadedIndexedDisplayStackSetter implements BackgroundSetter, ThreadedProducer {

    private ThreadedDisplayUpdateConsumer delegate;

    private IterableObjectGenerator<DisplayStack, DisplayStack> stackGenerator;

    public void init(
            CheckedFunction<Integer, DisplayStack, ? extends Throwable> displayStacks,
            InteractiveThreadPool threadPool,
            ErrorReporter errorReporter) {

        stackGenerator = new DisplayStackGenerator("display");

        delegate =
                new ThreadedDisplayUpdateConsumer(
                        ensure8bit(displayStacks), 0, threadPool, errorReporter);
    }

    // How it provides stacks to other applications (the output)
    public DisplayUpdateRememberStack getStackProvider() {
        return delegate;
    }

    // How it is updated with indexes from other classes (the input control mechanism)
    public IndexGettableSettable getIndexGettableSettable() { // NOSONAR
        return delegate;
    }

    public int getIndex() {
        return delegate.getIndex();
    }

    @Override
    public void setImageStackContainer(
            CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException>
                    imageStackCntr) {

        delegate.setImageStackGenerator(ensure8bit(imageStackCntr));
        delegate.update();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    private CheckedFunction<Integer, DisplayUpdate, BackgroundStackContainerException> ensure8bit(
            CheckedFunction<Integer, DisplayStack, ? extends Throwable> cntr) {
        return new NoOverlayBridgeFromGenerator(
                new IterableObjectGeneratorBridge<>(
                        stackGenerator, new EnsureUnsigned8Bit<>(cntr)));
    }
}
