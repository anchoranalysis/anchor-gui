package org.anchoranalysis.gui.frame.canvas;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventListener;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.swing.event.EventListenerList;
import lombok.AllArgsConstructor;

/**
 * Broadcasts and a changed event to many listeners.
 * 
 * @author Owen Feehan
 * @param <T> listener-type
 */
@AllArgsConstructor
public class BroadcastMouseEvents<T extends EventListener> {

    private MouseEventCreator eventCreator;
    private EventListenerList receivers;
    private Class<T> listenerType;
        
    public void sendChangedEvent( MouseEvent event, boolean canBeIgnored, BiConsumer<T,MouseEvent> consumer) {
        if (!isIgnored(event,canBeIgnored)) {
            getListeners().forEach( listener -> 
                consumer.accept(listener, eventCreator.mouseEventNew(event))
            );    
        }
    }
    
    private Stream<T> getListeners() {
        return Arrays.stream(receivers.getListeners(listenerType));
    }

    private boolean isIgnored(MouseEvent event, boolean canBeIgnored) {
        return canBeIgnored && (event.isControlDown() || event.isShiftDown());
    }
}
