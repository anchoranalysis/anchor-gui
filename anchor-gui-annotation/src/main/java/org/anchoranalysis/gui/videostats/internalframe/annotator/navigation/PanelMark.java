/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IConfirmReset;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.IUndoRedo;

// A panel for selecting tools
public class PanelMark extends PanelWithLabel implements IIsAcceptedRejected {

    private JButton buttonConfirm;
    private JButton buttonReset;
    private JButton buttonUndoRedo;
    private JCheckBox checkAccepted;
    private IUndoRedo undoRedo;
    private IConfirmReset confirmReset;
    private IToolPanelListenFramework panelTool;

    public PanelMark(
            IConfirmReset confirmReset, IUndoRedo undoRedo, IToolPanelListenFramework panelTool) {
        this.undoRedo = undoRedo;
        this.panelTool = panelTool;
        this.confirmReset = confirmReset;

        buttonConfirm = new JButton("Confirm");
        buttonUndoRedo = new JButton("Undo");
        buttonReset = new JButton("Reset");

        checkAccepted = new JCheckBox("Acc");
        checkAccepted.setSelected(true);

        super.init("");

        setLabelForeground(Color.RED);

        disableFocusOnButtons();

        UndoRedoStateChanged undoRedoStateChanged =
                new UndoRedoStateChanged(buttonUndoRedo, undoRedo);
        undoRedoStateChanged.refresh();

        buttonUndoRedo.addActionListener(new UndoAction());
        buttonReset.addActionListener(new ResetAction());

        undoRedo.addUndoRedoStateChangedListener(undoRedoStateChanged);

        addConfirmResetListeners();
        refreshConfirmReset();

        panelTool.addToolSwitchedListener(
                new ToolSwitchedListener() {

                    @Override
                    public void toolSwitched() {
                        refreshConfirmReset();
                    }
                });
    }

    public void addActionListenerConfirm(ActionListener l) {
        buttonConfirm.addActionListener(l);
    }

    public void dispose() {
        getPanel().removeAll();
        panelTool = null;
    }

    @Override
    public boolean isAccepted() {
        return checkAccepted.isSelected();
    }

    @Override
    protected JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        panel.add(buttonConfirm);
        panel.add(buttonReset);
        panel.add(buttonUndoRedo);
        panel.add(checkAccepted);
        return panel;
    }

    private void disableFocusOnButtons() {
        buttonConfirm.setFocusable(false);
        buttonUndoRedo.setFocusable(false);
        buttonReset.setFocusable(false);
        checkAccepted.setFocusable(false);
    }

    private class UndoAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            if (undoRedo.canUndo()) {
                undoRedo.undo();
                addConfirmResetListeners();
                refreshConfirmReset();
            } else if (undoRedo.canRedo()) {
                undoRedo.redo();
                addConfirmResetListeners();
                refreshConfirmReset();
            }
        }
    }

    private class ResetAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (confirmReset.canReset()) {
                confirmReset.reset();
            }
        }
    }

    private void addConfirmResetListeners() {
        confirmReset.addConfirmResetStateChangedListener(
                new ConfirmResetStateChangedListener() {

                    @Override
                    public void confirmResetStateChanged() {
                        refreshConfirmReset();
                    }
                });
    }

    private void refreshConfirmReset() {
        buttonConfirm.setEnabled(confirmReset.canConfirm() && !panelTool.isSelectedDelete());
        buttonReset.setEnabled(confirmReset.canReset() && !panelTool.isSelectedDelete());
    }
}
