/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

class SwitchToolMenu {

    private JMenu delegate = new JMenu("Mouse Pointer");
    private PanelTool panelTool;

    public SwitchToolMenu(final PanelTool panelTool) {
        super();
        this.panelTool = panelTool;

        ButtonGroup group = new ButtonGroup();

        final JRadioButtonMenuItem guess = new JRadioButtonMenuItem(new GuessAction());
        final JRadioButtonMenuItem selectPoints =
                new JRadioButtonMenuItem(new SelectPointsAction());
        final JRadioButtonMenuItem delete = new JRadioButtonMenuItem(new DeleteAction());

        delegate.addMenuListener(
                new MenuListener() {

                    @Override
                    public void menuSelected(MenuEvent e) {
                        guess.setSelected(panelTool.isSelectedGuess());
                        selectPoints.setSelected(panelTool.isSelectedSelectPoints());
                        delete.setSelected(panelTool.isSelectedDelete());
                    }

                    @Override
                    public void menuDeselected(MenuEvent e) {}

                    @Override
                    public void menuCanceled(MenuEvent e) {}
                });

        delegate.add(guess);
        group.add(guess);

        delegate.add(selectPoints);
        group.add(selectPoints);

        delegate.add(selectPoints);
        group.add(selectPoints);

        delegate.add(delete);
        group.add(delete);
    }

    public JMenu getMenu() {
        return delegate;
    }

    public class GuessAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public GuessAction() {
            super("Guess");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            panelTool.switchToGuess();
        }
    }

    public class SelectPointsAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = 1L;

        public SelectPointsAction() {
            super("Select Points");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            panelTool.switchToSelectPoints();
        }
    }

    public class DeleteAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = 1L;

        public DeleteAction() {
            super("Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            panelTool.switchToDelete();
        }
    }
}
