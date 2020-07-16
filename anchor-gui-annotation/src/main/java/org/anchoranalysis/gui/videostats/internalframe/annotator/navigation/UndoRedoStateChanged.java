/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import javax.swing.JButton;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.IUndoRedo;

class UndoRedoStateChanged implements UndoRedoStateChangedListener {

    private JButton buttonUndoRedo;
    private IUndoRedo controller;

    public UndoRedoStateChanged(JButton buttonUndoRedo, IUndoRedo controller) {
        super();
        this.buttonUndoRedo = buttonUndoRedo;
        this.controller = controller;
    }

    public void refresh() {
        if (controller.canRedo()) {
            buttonUndoRedo.setText("Redo");
            buttonUndoRedo.setEnabled(true);
        } else if (controller.canUndo()) {
            buttonUndoRedo.setText("Undo");
            buttonUndoRedo.setEnabled(true);
        } else {
            buttonUndoRedo.setText("Undo");
            buttonUndoRedo.setEnabled(false);
        }
    }

    @Override
    public void undoRedoStateChanged() {
        refresh();
    }
}
