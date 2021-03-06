/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.videostats.module;

import java.util.Collection;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.event.RoutableEventSourceObject;
import org.anchoranalysis.core.event.RoutableReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.property.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.AssociatedEnergyStackGetter;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;

public class VideoStatsModule implements RoutableEventSourceObject {

    private JInternalFrame component;
    private EventListenerList eventListenerList = new EventListenerList();

    public void addModuleClosedListener(VideoStatsModuleClosedListener l) {
        eventListenerList.add(VideoStatsModuleClosedListener.class, l);
    }

    public void triggerModuleClosedEvents() {
        // We trigger a ModuleClosedEvent for other objects to react to
        for (VideoStatsModuleClosedListener l :
                eventListenerList.getListeners(VideoStatsModuleClosedListener.class)) {
            l.videoStatsModuleClosed(new VideoStatsModuleClosedEvent(this, this));
        }
    }

    public static class ReceivableSendablePair<T> {

        private RoutableReceivable<PropertyValueChangeEvent<T>> receivable;
        private IPropertyValueSendable<T> sendable;

        public RoutableReceivable<PropertyValueChangeEvent<T>> getReceivable() {
            return receivable;
        }

        public void setReceivable(RoutableReceivable<PropertyValueChangeEvent<T>> receivable) {
            this.receivable = receivable;
        }

        public IPropertyValueSendable<T> getSendable() {
            return sendable;
        }

        public void setSendable(IPropertyValueSendable<T> sendable) {
            this.sendable = sendable;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class ReceivableSendablePairMap {

        private HashMap<String, ReceivableSendablePair> map = new HashMap<>();

        public void add(String key, ReceivableSendablePair p) {
            map.put(key, p);
        }

        public ReceivableSendablePair get(String key) {
            return map.get(key);
        }

        public Collection<ReceivableSendablePair> getPairs() {
            return map.values();
        }
    }

    private ReceivableSendablePairMap receivableSendablePairMap = new ReceivableSendablePairMap();

    private IChangeMarkDisplaySendable selectChangeMarkDisplaySendable;

    private AssociatedEnergyStackGetter energyStackGetter;

    private boolean fixedSize = false;

    public JInternalFrame getComponent() {
        return component;
    }

    public void setComponent(JInternalFrame component) {
        this.component = component;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public IChangeMarkDisplaySendable getChangeMarkDisplaySendable() {
        return selectChangeMarkDisplaySendable;
    }

    public void setChangeMarkDisplaySendable(IChangeMarkDisplaySendable sendable) {
        this.selectChangeMarkDisplaySendable = sendable;
    }

    public ReceivableSendablePairMap getReceivableSendablePairMap() {
        return receivableSendablePairMap;
    }

    public AssociatedEnergyStackGetter getEnergyStackGetter() {
        return energyStackGetter;
    }

    public void setEnergyStackGetter(AssociatedEnergyStackGetter energyStackGetter) {
        this.energyStackGetter = energyStackGetter;
    }
}
