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
import org.anchoranalysis.core.event.RoutableEvent;
import org.anchoranalysis.core.event.RoutableListener;
import org.anchoranalysis.core.event.RoutableReceivable;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.image.OverlaysWithEnergyStack;
import org.anchoranalysis.gui.property.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.AssociatedEnergyStackGetter;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;
import org.anchoranalysis.overlay.collection.OverlayCollection;

// Triggers a OverlayCollectionWithStack event, every time a OverlayCollection event occurs
class OverlayCollectionWithStackAdaptorRouted
        implements RoutableReceivable<PropertyValueChangeEvent<OverlaysWithEnergyStack>> {

    private List<RoutableListener<PropertyValueChangeEvent<OverlaysWithEnergyStack>>> listeners =
            new ArrayList<>();

    public OverlayCollectionWithStackAdaptorRouted(
            RoutableReceivable<PropertyValueChangeEvent<OverlayCollection>> source,
            final AssociatedEnergyStackGetter associatedRasterGetter,
            final InteractiveThreadPool threadPool,
            final ErrorReporter errorReporter) {

        source.addRoutableListener(
                new RoutableListener<PropertyValueChangeEvent<OverlayCollection>>() {

                    @Override
                    public void eventOccurred(
                            RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt) {

                        TriggerEvents triggerEvents =
                                new TriggerEvents(evt, associatedRasterGetter, errorReporter);
                        threadPool.submit(triggerEvents, "Trigger Events");
                    }
                });
    }

    private class TriggerEvents extends InteractiveWorker<EnergyStack, Void> {

        private AssociatedEnergyStackGetter energyStackGetter;
        private ErrorReporter errorReporter;
        private RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt;

        public TriggerEvents(
                RoutableEvent<PropertyValueChangeEvent<OverlayCollection>> evt,
                AssociatedEnergyStackGetter energyStackGetter,
                ErrorReporter errorReporter) {
            super();
            this.energyStackGetter = energyStackGetter;
            this.errorReporter = errorReporter;
            this.evt = evt;
        }

        @Override
        protected EnergyStack doInBackground() throws Exception {
            return energyStackGetter.getAssociatedEnergyStack();
        }

        @Override
        protected void done() {

            try {

                for (RoutableListener<PropertyValueChangeEvent<OverlaysWithEnergyStack>> l :
                        listeners) {

                    // get() gets the EnergyStack
                    PropertyValueChangeEvent<OverlaysWithEnergyStack> evtNew =
                            new PropertyValueChangeEvent<>(
                                    evt.getSource(),
                                    new OverlaysWithEnergyStack(evt.getEvent().getValue(), get()),
                                    evt.getEvent().isAdjusting());

                    RoutableEvent<PropertyValueChangeEvent<OverlaysWithEnergyStack>>
                            routableEventNew = new RoutableEvent<>(evt.getRoutableSource(), evtNew);

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
    public void addRoutableListener(
            RoutableListener<PropertyValueChangeEvent<OverlaysWithEnergyStack>> l) {
        listeners.add(l);
    }

    @Override
    public void removeRoutableListener(
            RoutableListener<PropertyValueChangeEvent<OverlaysWithEnergyStack>> l) {
        listeners.remove(l);
    }
}
