package org.anchoranalysis.gui.frame.threaded.stack;

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


import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

/**
 * Retrieves DisplayUpdates when an index is changed, or when an explicit update command is set.
 * 
 * Ignores any updates that are the same as previous, or NULL.
 * 
 * @author Owen Feehan
 *
 */
public class ThreadedDisplayUpdateConsumer implements IDisplayUpdateRememberStack, IIndexGettableSettable {

	private class UpdateSignal {
		
		private boolean needsUpdate = false;
		
		public boolean isInNeedOfUpdate() {
			return needsUpdate;
		}
		
		public synchronized void signalNeedOfUpdate() {
			needsUpdate = true;
			updateMonitor.notifyAll();
		}
		
		public synchronized void clearUpdateNeed() {
			needsUpdate = false;
		}
	}
	
	private UpdateSignal updateMonitor = new UpdateSignal();
 
	private int index = -1;
	
	private DisplayUpdate currentUpdate = null;
	private BoundOverlayedDisplayStack currentDisplayStack = null;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	//private static Log log = LogFactory.getLog(ThreadedIndexedImageStackProvider.class);
	
	private UpdateImage updateImage;
	
	private IObjectBridge<Integer, DisplayUpdate> displayUpdateBridge;
	//private IterableObjectGenerator<Integer,OverlayedDisplayStack> imageStackGenerator;

	private ErrorReporter errorReporter;
	
	private class UpdateImage extends InteractiveWorker<Integer, Integer> {
		
		@Override
		protected Integer doInBackground() {
		    
			while(true) {
				
				synchronized(updateMonitor) {
					if (!updateMonitor.isInNeedOfUpdate()) {
						try {
							updateMonitor.wait();
						} catch (InterruptedException e) {
							return 0;
						}
						continue;
					}
					updateMonitor.clearUpdateNeed();
				}
					
				try {
					updateImage();
				} catch (Exception e) {
					errorReporter.recordError(ThreadedDisplayUpdateConsumer.class, e);
					return 0;
				}
		
			}
		}
		
		// Updates the current stack and notifies listeners
		public void updateImage() {
			try {
				currentUpdate = displayUpdateBridge.bridgeElement(index);
				
				if (currentUpdate==null) {
					return;
				}
				
				if (currentUpdate.getDisplayStack()!=null) {
					currentDisplayStack = currentUpdate.getDisplayStack();
				}

			} catch (BridgeElementException e) {
				currentUpdate = null;
				errorReporter.recordError(ThreadedDisplayUpdateConsumer.class, e);
			} finally {
				publish( Integer.valueOf(1) );
				
			}
		}
		
		protected void process(List<Integer> pairs) {
			for (ChangeListener cl : eventListenerList.getListeners(ChangeListener.class)) {
				cl.stateChanged( new ChangeEvent(this));
			}
		}

		@Override
		protected void done() {
			super.done();
		}
		
	}
	
	public ThreadedDisplayUpdateConsumer( IObjectBridge<Integer,DisplayUpdate> displayUpdateBridge, int defaultIndex, InteractiveThreadPool threadPool, ErrorReporter errorReporter ) {
		super();
		this.displayUpdateBridge = displayUpdateBridge;
		this.index = defaultIndex;
		
		this.errorReporter = errorReporter;
		
		// Create an empty stack as the first state, and notify a change
		updateImage = new UpdateImage();
		updateImage.updateImage();
		threadPool.submit( updateImage, "Update image" );
	}
	
	public synchronized void setImageStackGenerator( IObjectBridge<Integer, DisplayUpdate> displayUpdateBridge ) {
		this.displayUpdateBridge = displayUpdateBridge;
	}
	
	@Override
	public DisplayUpdate get() throws GetOperationFailedException {
		
		if (currentUpdate==null) {
			throw new GetOperationFailedException("currentImage is null. No update to return");
		}
		
		return currentUpdate;
	}

	@Override
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add( ChangeListener.class, cl );
	}
		
	public synchronized void update() {
		updateMonitor.signalNeedOfUpdate();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		
		if (this.index!=index) {
			this.index = index;
			updateMonitor.signalNeedOfUpdate();
		}
	}
	
	// Must be called to end the current operation
	public void dispose() {
		updateImage.cancel(true);
		eventListenerList = null;
		updateImage = null;
		currentUpdate = null;
		updateMonitor = null;
		displayUpdateBridge = null;
	}

	@Override
	public BoundOverlayedDisplayStack getCurrentDisplayStack()
			throws GetOperationFailedException {
		return currentDisplayStack;
	}


}
