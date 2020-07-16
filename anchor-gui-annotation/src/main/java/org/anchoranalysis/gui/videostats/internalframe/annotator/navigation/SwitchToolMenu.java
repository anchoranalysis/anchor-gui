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
