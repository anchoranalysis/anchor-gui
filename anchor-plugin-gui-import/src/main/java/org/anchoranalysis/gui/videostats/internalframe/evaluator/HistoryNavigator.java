/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.anchoranalysis.gui.frame.overlays.IShowEvaluationResult;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;

public class HistoryNavigator {

    private JPanel panel = new JPanel();

    private ArrayList<ProposedMarks> history;

    private int historyMaxSize = 100;

    private int currentIndex;

    private JButton buttonForward;
    private JButton buttonBackward;

    private IShowEvaluationResult showEvaluationResult;

    private class ForwardAction extends AbstractAction {

        private static final long serialVersionUID = 4487788498299411161L;

        public ForwardAction() {
            super(">");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {

            if (currentIndex != (history.size() - 1)) {
                currentIndex++;
                showIndex(currentIndex);
            }
        }
    }

    private class BackwardAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = -4439921001202167371L;

        public BackwardAction() {
            super("<");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {

            if (currentIndex > 0) {
                currentIndex--;
                showIndex(currentIndex);
            }
        }
    }

    public HistoryNavigator(IShowEvaluationResult showEvaluationResult) {

        AbstractAction backwardAction = new BackwardAction();
        AbstractAction forwardAction = new ForwardAction();

        buttonBackward = new JButton(backwardAction);
        buttonForward = new JButton(forwardAction);

        panel.add(buttonBackward);
        panel.add(buttonForward);

        history = new ArrayList<>();
        currentIndex = -1;
        this.showEvaluationResult = showEvaluationResult;

        KeyStroke left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK);
        KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK);

        // KeyStroke left = KeyStroke.getKeyStroke( "LEFT" );
        // KeyStroke right = KeyStroke.getKeyStroke( "RIGHT" );

        panel.registerKeyboardAction(
                backwardAction, "backward", left, JComponent.WHEN_IN_FOCUSED_WINDOW);

        panel.registerKeyboardAction(
                forwardAction, "forward", right, JComponent.WHEN_IN_FOCUSED_WINDOW);

        /*buttonBackward.getActionMap().put("backwardAction", backwardAction);
        buttonBackward.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( (KeyStroke) backwardAction.getValue(Action.ACCELERATOR_KEY), "backwardAction");

        buttonBackward.getActionMap().put("forwardAction", forwardAction);
        buttonBackward.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( (KeyStroke) forwardAction.getValue(Action.ACCELERATOR_KEY), "forwardAction");*/
    }

    public void add(ProposedMarks er) {

        if (currentIndex != (history.size() - 1)) {

            while (history.size() > (currentIndex + 1)) {
                history.remove(history.size() - 1);
            }
        }

        // We are in final position so we just add
        history.add(er);

        // History ma
        if (history.size() == historyMaxSize) {
            history.remove(0);
        }

        currentIndex = history.size() - 1;

        showIndex(currentIndex);
    }

    private void showIndex(int index) {
        ProposedMarks er = history.get(index);
        showEvaluationResult.showEvaluationResult(er, null);

        if (index == (history.size() - 1)) {
            buttonForward.setEnabled(false);
        } else {
            buttonForward.setEnabled(true);
        }

        if (index == 0) {
            buttonBackward.setEnabled(false);
        } else {
            buttonBackward.setEnabled(true);
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}
