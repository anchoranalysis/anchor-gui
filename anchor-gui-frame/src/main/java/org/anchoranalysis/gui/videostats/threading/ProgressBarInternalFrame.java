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



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.apache.commons.lang.time.StopWatch;

public class ProgressBarInternalFrame {
	
	private JProgressBar progressBar;
	private JInternalFrame internalFrame;
	private Timer timer;
	
	public ProgressBarInternalFrame( String name ) {
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setString("");
		
		JPanel center_panel = new JPanel();
		center_panel.add( new JLabel(name) );
		center_panel.add(progressBar);
		
		internalFrame = new JInternalFrame("Working...");
		internalFrame.setClosable(true);
		internalFrame.getContentPane().add(center_panel, BorderLayout.CENTER);
		internalFrame.pack();
		
		
		final StopWatch sw = new StopWatch();
		
		ActionListener timerListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (progressBar!=null && progressBar.isIndeterminate()==true) {
					long time = sw.getTime()/1000;
					progressBar.setString( String.format("%ds",time) );
				}
			}
		};
		
		sw.start();
		timer = new Timer(1000, timerListener);
		timer.setRepeats(true);
		timer.start();
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JInternalFrame getInternalFrame() {
		return internalFrame;
	}
	
	
	private class UpdateProgressBar implements PropertyChangeListener {

		private SwingWorker<?,?> sw;
		
		public UpdateProgressBar(SwingWorker<?, ?> sw) {
			super();
			this.sw = sw;
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			if (progressBar==null) {
				return;
			}
			
			int progress = sw.getProgress();
			
			if (progress!=progressBar.getMaximum()) {
				progressBar.setValue( progress );
				
				// We assume once we've received the first report of progress above 0 the
				//  indeterminate part is now over
				if (progress!=0) {
					progressBar.setIndeterminate(false);
					timer.stop();
					progressBar.setString(null);
					
				}
			} else {
				internalFrame.dispose();
				internalFrame = null;
				progressBar = null;
				timer = null;
			}
			
		}
		
	}
	
	
	public PropertyChangeListener createChangeListener( final SwingWorker<?,?> sw ) {
		return new UpdateProgressBar(sw);
	}
}
