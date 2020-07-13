package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/** Provides a method for updating a display stack in response to index changes, or setImageStackCntr() */
public class ThreadedIndexedDisplayStackSetter implements IBackgroundSetter, IThreadedProducer {

	private ThreadedDisplayUpdateConsumer delegate;
	
	private IterableObjectGenerator<DisplayStack, DisplayStack> stackGenerator;
	
	public void init(
		FunctionWithException<Integer,DisplayStack,? extends Throwable> cntrDisplayStack,
		InteractiveThreadPool threadPool,
		ErrorReporter errorReporter
	) throws InitException {

		stackGenerator = new DisplayStackGenerator("display" );
	
		delegate = new ThreadedDisplayUpdateConsumer(
			ensure8bit(cntrDisplayStack),
			0,
			threadPool,
			errorReporter
		);		
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
	public void setImageStackCntr( FunctionWithException<Integer,DisplayStack,GetOperationFailedException> imageStackCntr ) {
		
		delegate.setImageStackGenerator(
			ensure8bit(imageStackCntr)
		);
		delegate.update();
	}

	@Override
	public void dispose() {
		delegate.dispose();
	}
	
	private FunctionWithException<Integer, DisplayUpdate,OperationFailedException> ensure8bit( FunctionWithException<Integer,DisplayStack,? extends Throwable> cntr ) {
		return new NoOverlayBridgeFromGenerator(
			new IterableObjectGeneratorBridge<>(
				stackGenerator,
				new EnsureUnsigned8Bit<>(cntr)
			)
		);
	}

}
