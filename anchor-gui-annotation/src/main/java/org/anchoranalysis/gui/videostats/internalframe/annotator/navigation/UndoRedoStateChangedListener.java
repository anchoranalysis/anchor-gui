/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.util.EventListener;

public interface UndoRedoStateChangedListener extends EventListener {

    public void undoRedoStateChanged();
}
