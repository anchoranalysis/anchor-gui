/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo;

import com.google.common.base.Supplier;
import java.util.function.Consumer;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.UndoRedoStateChangedListener;

public class UndoRedoRecorder<T> implements IUndoRedo, IRecordSnapshot {

    private Runnable showCurrentState;

    /** Copy from current state */
    private Supplier<T> copyFromCurrentState;

    /** Write to current-state */
    private Consumer<T> updateCurrentState;

    public UndoRedoRecorder(
            Runnable showCurrentState,
            Supplier<T> copyFromCurrentState,
            Consumer<T> updateCurrentState) {
        super();
        this.showCurrentState = showCurrentState;
        this.copyFromCurrentState = copyFromCurrentState;
        this.updateCurrentState = updateCurrentState;
    }

    // These are other states which we can undo, or redo to
    private T undoState; // Can be NULL
    private T redoState; // Can be NULL

    private EventListenerList listeners = new EventListenerList();

    @Override
    public boolean canUndo() {
        return undoState != null;
    }

    @Override
    public boolean canRedo() {
        return redoState != null;
    }

    @Override
    public void undo() {
        if (undoState == null) {
            return;
        }

        redoState = copyFromCurrentState.get();
        updateCurrentState.accept(undoState);
        undoState = null;
        showUpdatedState();
    }

    @Override
    public void redo() {
        if (redoState == null) {
            return;
        }

        undoState = copyFromCurrentState.get();
        updateCurrentState.accept(redoState);
        redoState = null;
        showUpdatedState();
    }

    private void showUpdatedState() {
        showCurrentState.run();
        triggerUndoRedoStateChangedEvent();
    }

    @Override
    public void addUndoRedoStateChangedListener(UndoRedoStateChangedListener listener) {
        listeners.add(UndoRedoStateChangedListener.class, listener);
    }

    /** Records the current state, that can be reverted to */
    @Override
    public void recordSnapshot() {
        undoState = copyFromCurrentState.get();
        redoState = null;
        triggerUndoRedoStateChangedEvent();
    }

    public void dispose() {
        if (undoState != null) {
            undoState = null;
        }
        if (redoState != null) {
            redoState = null;
        }
        listeners = null;
    }

    private void triggerUndoRedoStateChangedEvent() {
        for (UndoRedoStateChangedListener e :
                listeners.getListeners(UndoRedoStateChangedListener.class)) {
            e.undoRedoStateChanged();
        }
    }
}
