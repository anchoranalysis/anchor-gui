/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo;

import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.UndoRedoStateChangedListener;

public interface IUndoRedo {

    boolean canUndo();

    boolean canRedo();

    void undo();

    void redo();

    void addUndoRedoStateChangedListener(UndoRedoStateChangedListener listener);
}
