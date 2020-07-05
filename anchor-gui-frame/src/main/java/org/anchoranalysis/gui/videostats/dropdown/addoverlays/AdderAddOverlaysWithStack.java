package org.anchoranalysis.gui.videostats.dropdown.addoverlays;



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


import javax.swing.JFrame;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.event.IRoutableReceivable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class AdderAddOverlaysWithStack implements IAddVideoStatsModule {

	private final IAddVideoStatsModule delegate;
	private final InteractiveThreadPool threadPool;
	private final ErrorReporter errorReporter;
	
	public AdderAddOverlaysWithStack(IAddVideoStatsModule adder, InteractiveThreadPool threadPool, ErrorReporter errorReporter) {
		super();
		this.delegate = adder;
		this.errorReporter = errorReporter;
		this.threadPool = threadPool;
	}

	@Override
	public void addVideoStatsModule(VideoStatsModule module) {

		LinkModules link = new LinkModules(module);
		
		// If we have an OVERLAYERS event, but don't already have a OVERLAYS_WITH_STACK we plug the gap
		//  by adding a stack 
		if (link.getOverlays().exists() && !link.getOverlaysWithStack().exists() && module.getNrgStackGetter()!=null)
		{
			IRoutableReceivable<PropertyValueChangeEvent<OverlayCollection>> rec = link.getOverlays().getReceivable();
			if (rec!=null) {
			
				link.getOverlaysWithStack().add(
					new OverlayCollectionWithStackAdaptorRouted(
						rec,
						module.getNrgStackGetter(),
						threadPool,
						errorReporter
					)		
				);
			}
			
		} else {
			System.out.printf("addVideoStatsModule without OVERLAYS_WITH_STACK%n");
		}

		delegate.addVideoStatsModule(module);		
	}

	@Override
	public VideoStatsModuleSubgroup getSubgroup() {
		return delegate.getSubgroup();
	}

	@Override
	public JFrame getParentFrame() {
		return delegate.getParentFrame();
	}

	@Override
	public IAddVideoStatsModule createChild() {
		return delegate.createChild();
	}
	
}