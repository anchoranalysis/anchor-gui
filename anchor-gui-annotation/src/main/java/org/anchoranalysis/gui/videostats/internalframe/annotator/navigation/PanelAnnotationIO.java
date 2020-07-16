/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.ISaveActionListenerFactory;

// A panel for selecting tools
public class PanelAnnotationIO extends PanelWithLabel {

    private JButton buttonSkipAnnotation;
    private JButton buttonSaveUnfinished; // Save to return to later
    private JButton buttonSaveFinished;

    public PanelAnnotationIO() {
        buttonSkipAnnotation = new JButton("Skip");
        buttonSaveUnfinished = new JButton("Save");
        buttonSaveFinished = new JButton("Finish");
        super.init("Annotation");

        buttonSkipAnnotation.setFocusable(false);
        buttonSaveUnfinished.setFocusable(false);
        buttonSaveFinished.setFocusable(false);
    }

    public void addActionsToSavePanel(ISaveActionListenerFactory factory, JInternalFrame frame) {

        addActionListenerSaveFinished(factory.saveFinished(frame));
        addActionListenerSaveUnfinished(factory.savePaused(frame));
        addActionListenerSkipAnnotation(factory.skipAnnotation(frame));
    }

    @Override
    protected JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        panel.add(buttonSkipAnnotation);
        panel.add(buttonSaveUnfinished);
        panel.add(buttonSaveFinished);
        return panel;
    }

    private void addActionListenerSaveUnfinished(ActionListener l) {
        buttonSaveUnfinished.addActionListener(l);
    }

    private void addActionListenerSaveFinished(ActionListener l) {
        buttonSaveFinished.addActionListener(l);
    }

    private void addActionListenerSkipAnnotation(ActionListener l) {
        buttonSkipAnnotation.addActionListener(l);
    }
}
