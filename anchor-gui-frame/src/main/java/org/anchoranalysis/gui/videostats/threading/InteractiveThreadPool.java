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
package org.anchoranalysis.gui.videostats.threading;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JInternalFrame;

// A thread pool for all SwingWorkers created at any given time
public class InteractiveThreadPool {

	private ExecutorService executor;
	private AddProgressBarInternalFrame internalFrameAdder;
	
	public static interface AddProgressBarInternalFrame {
		void addInternalFrame( JInternalFrame frame );
	}
	
	public InteractiveThreadPool( AddProgressBarInternalFrame internalFrameAdder ) {
		this.internalFrameAdder = internalFrameAdder;
		executor = Executors.newCachedThreadPool();
	}
	
	public void submit( InteractiveWorker<?,?> sw, String name ) {
		
		//System.out.printf("SUBMIT: %s\n", name );
		//sw.execute();
		
		executor.submit(sw);
	}

	
	public void submitWithProgressMonitor( final InteractiveWorker<?,?> sw, String name ) {
		
		//System.out.printf("SUBMIT: %s\n", name );
		
		ProgressBarInternalFrame progressBarFrame = new ProgressBarInternalFrame( name );
		
		internalFrameAdder.addInternalFrame( progressBarFrame.getInternalFrame() );
		sw.addPropertyChangeListener( progressBarFrame.createChangeListener(sw) );
		
		executor.submit(sw);
	}

}
