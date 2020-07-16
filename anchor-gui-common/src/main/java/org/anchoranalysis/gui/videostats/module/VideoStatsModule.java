/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import java.util.Collection;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.event.IRoutableEventSourceObject;
import org.anchoranalysis.core.event.IRoutableReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;

public class VideoStatsModule implements IRoutableEventSourceObject {

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

        private IRoutableReceivable<PropertyValueChangeEvent<T>> receivable;
        private IPropertyValueSendable<T> sendable;

        public IRoutableReceivable<PropertyValueChangeEvent<T>> getReceivable() {
            return receivable;
        }

        public void setReceivable(IRoutableReceivable<PropertyValueChangeEvent<T>> receivable) {
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

    private INRGStackGetter nrgStackGetter;

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

    public INRGStackGetter getNrgStackGetter() {
        return nrgStackGetter;
    }

    public void setNrgStackGetter(INRGStackGetter nrgStackGetter) {
        this.nrgStackGetter = nrgStackGetter;
    }
}
