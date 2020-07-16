/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.frame.threaded.stack.IThreadedProducer;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedDisplayUpdateConsumer;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.image.io.generator.raster.DisplayStackGenerator;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;

public class ThreadedIndexedDisplayStackSetter implements IBackgroundSetter, IThreadedProducer {

    private ThreadedDisplayUpdateConsumer delegate;

    private IterableObjectGenerator<DisplayStack, DisplayStack> stackGenerator;

    public void init(
            FunctionWithException<Integer, DisplayStack, ? extends Throwable> cntrDisplayStack,
            InteractiveThreadPool threadPool,
            ErrorReporter errorReporter)
            throws InitException {

        stackGenerator = new DisplayStackGenerator("display");

        delegate =
                new ThreadedDisplayUpdateConsumer(
                        ensure8bit(cntrDisplayStack), 0, threadPool, errorReporter);
    }

    // How it provides stacks to other applications (the output)
    public IDisplayUpdateRememberStack getStackProvider() {
        return delegate;
    }

    // How it is updated with indexes from other classes (the input control mechanism)
    public IIndexGettableSettable getIndexGettableSettable() {
        return delegate;
    }

    public int getIndex() {
        return delegate.getIndex();
    }

    @Override
    public void setImageStackCntr(
            FunctionWithException<Integer, DisplayStack, GetOperationFailedException>
                    imageStackCntr) {

        delegate.setImageStackGenerator(ensure8bit(imageStackCntr));
        delegate.update();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    private FunctionWithException<Integer, DisplayUpdate, OperationFailedException> ensure8bit(
            FunctionWithException<Integer, DisplayStack, ? extends Throwable> cntr) {
        return new NoOverlayBridgeFromGenerator(
                new IterableObjectGeneratorBridge<>(
                        stackGenerator, new EnsureUnsigned8Bit<>(cntr)));
    }
}
