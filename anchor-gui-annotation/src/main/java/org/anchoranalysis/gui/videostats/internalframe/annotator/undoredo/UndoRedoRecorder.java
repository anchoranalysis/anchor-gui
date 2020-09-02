/*-
 * #%L
 * anchor-gui-annotation
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
    private T undoState; // Can be null
    private T redoState; // Can be null

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
