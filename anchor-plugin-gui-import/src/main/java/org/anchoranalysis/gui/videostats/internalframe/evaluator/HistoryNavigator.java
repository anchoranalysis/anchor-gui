/* (C)2020 */
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
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

public class HistoryNavigator {

    private JPanel panel = new JPanel();

    private ArrayList<ProposedCfg> history;

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

    public void add(ProposedCfg er) {

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
        ProposedCfg er = history.get(index);
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
