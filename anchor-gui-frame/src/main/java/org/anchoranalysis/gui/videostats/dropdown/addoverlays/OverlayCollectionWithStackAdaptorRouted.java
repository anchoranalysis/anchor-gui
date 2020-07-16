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
package org.anchoranalysis.gui.videostats.dropdown.addoverlays;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.event.IRoutableReceivable;
import org.anchoranalysis.core.event.RoutableEvent;
import org.anchoranalysis.core.event.RoutableListener;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

// Triggers a OverlayCollectionWithStack event, every time a OverlayCollection event occurs
class OverlayCollectionWithStackAdaptorRouted implements IRoutableReceivable<PropertyValueChangeEvent<OverlayCollectionWithImgStack>> {

	private List<RoutableListener<PropertyValueChangeEvent<OverlayCollectionWithImgStack>>> listeners = new ArrayList<>();
	
	public OverlayCollectionWithStackAdaptorRouted(
		IRoutableReceivable<PropertyValueChangeEvent<OverlayCollection>> source,
		final INRGStackGetter associatedRasterGetter,
		final InteractiveThreadPool threadPool,
		final ErrorReporter errorReporter
	) {
		
		source.addRoutableListener( new RoutableListener<PropertyValueChangeEvent<OverlayCollection>>(){

			@Override
			public void eventOccurred(
					RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt) {
				
				TriggerEvents triggerEvents = new TriggerEvents(
					evt,
					associatedRasterGetter,
					errorReporter
				);
				threadPool.submit(triggerEvents, "Trigger Events");
			}
		});
	}
	
	private class TriggerEvents extends InteractiveWorker<NRGStackWithParams, Void> {

		private INRGStackGetter nrgStackGetter;
		private ErrorReporter errorReporter;
		private RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt;
		
		public TriggerEvents(
			RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt,
			INRGStackGetter nrgStackGetter,
			ErrorReporter errorReporter
		) {
			super();
			this.nrgStackGetter = nrgStackGetter;
			this.errorReporter = errorReporter;
			this.evt = evt;
		}

		@Override
		protected NRGStackWithParams doInBackground()
				throws Exception {
			return nrgStackGetter.getAssociatedNrgStack();
		}

		@Override
		protected void done() {
			
			try {
			
				for (RoutableListener<PropertyValueChangeEvent<OverlayCollectionWithImgStack>> l : listeners) {
					
					// get() gets the NrgStack 
					PropertyValueChangeEvent<OverlayCollectionWithImgStack> evtNew =
						new PropertyValueChangeEvent<>(evt.getSource(),
						new OverlayCollectionWithImgStack( evt.getEvent().getValue(), get() ),
						evt.getEvent().getAdjusting()
					);
					
					RoutableEvent<PropertyValueChangeEvent<OverlayCollectionWithImgStack>> routableEventNew
						= new RoutableEvent<>(evt.getRoutableSource(), evtNew);
					
					l.eventOccurred(routableEventNew);
				}
			} catch (ExecutionException e) {
				errorReporter.recordError(OverlayCollectionWithStackAdaptorRouted.class, e);
			} catch (InterruptedException e) {
				errorReporter.recordError(OverlayCollectionWithStackAdaptorRouted.class, e);
			}
		}
		
	}
	
	@Override
	public void addRoutableListener( RoutableListener<PropertyValueChangeEvent<OverlayCollectionWithImgStack>> l) {
		listeners.add(l);
	}
	
	@Override
	public void removeRoutableListener( RoutableListener<PropertyValueChangeEvent<OverlayCollectionWithImgStack>> l) {
		listeners.remove(l);
	}

	
	
}
