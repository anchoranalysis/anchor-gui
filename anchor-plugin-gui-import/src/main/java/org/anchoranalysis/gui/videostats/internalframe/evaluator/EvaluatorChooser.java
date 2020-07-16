/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.ProposalOperationCreatorFromProposer;

public class EvaluatorChooser {

    private JPanel panel = new JPanel();

    private JComboBox<String> comboType;
    private JComboBox<String> comboProposer;
    private JComboBox<String> comboMarkEvaluator;

    private MarkEvaluatorSetForImage markEvaluatorSet;

    private Optional<ProposalOperationCreator> evaluator = Optional.empty();

    private List<ProposalOperationCreatorFromProposer<?>> listEvaluators = new ArrayList<>();
    private ErrorReporter errorReporter;

    private MarkEvaluatorResolved markEvaluatorSelected;

    private EventListenerList eventListenerList = new EventListenerList();

    public EvaluatorChooser(
            List<ProposalOperationCreatorFromProposer<?>> listEvaluators,
            final ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
        this.listEvaluators = listEvaluators;

        comboMarkEvaluator = new JComboBox<>();

        comboType = new JComboBox<>();

        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
            comboType.addItem(item.getEvaluatorName());
        }

        comboProposer = new JComboBox<>();

        panel.add(comboMarkEvaluator);
        panel.add(comboType);
        panel.add(comboProposer);

        comboMarkEvaluator.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectMarkEvaluator();
                    }
                });
        comboType.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        populateComboProposer();
                    }
                });
        comboProposer.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        @SuppressWarnings("unchecked")
                        JComboBox<String> cb = (JComboBox<String>) e.getSource();

                        String itemName = (String) cb.getSelectedItem();

                        if (itemName == null) {
                            return;
                        }

                        try {
                            evaluator = Optional.of(createProposerEvaluator(itemName));
                        } catch (CreateException e1) {
                            errorReporter.recordError(EvaluatorChooser.class, e1);
                        }
                    }
                });
    }

    public void init(MarkEvaluatorSetForImage markEvaluatorSet) {

        this.markEvaluatorSet = markEvaluatorSet;

        comboMarkEvaluator.addItem("");
        for (String key : markEvaluatorSet.keySet()) {
            comboMarkEvaluator.addItem(key);
        }

        selectMarkEvaluator();
    }

    public void selectMarkEvaluator() {

        populateComboProposer();
    }

    private String getEvaluatorName() {
        return (String) comboMarkEvaluator.getSelectedItem();
    }

    public void populateComboProposer() {

        comboProposer.removeAllItems();

        String evaluatorName = getEvaluatorName();

        if (evaluatorName == null || evaluatorName.isEmpty()) {
            markEvaluatorSelected = null;
            evaluator = Optional.empty();
            fireMarkEvaluatorChangedEvent();
            return;
        }

        String typeName = (String) comboType.getSelectedItem();

        // If it's called before the init
        if (typeName == null) {
            markEvaluatorSelected = null;
            evaluator = Optional.empty();
            fireMarkEvaluatorChangedEvent();
            return;
        }

        try {
            markEvaluatorSelected = markEvaluatorSet.get(evaluatorName);
        } catch (GetOperationFailedException e1) {
            errorReporter.recordError(EvaluatorChooser.class, e1);
        }

        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
            try {
                MPPInitParams so =
                        markEvaluatorSelected.getProposerSharedObjectsOperation().doOperation();
                item.init(so);
            } catch (CreateException e) {
                errorReporter.recordError(EvaluatorChooser.class, e);
            }
        }

        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {

            if (typeName.equals(item.getEvaluatorName())) {
                for (String name : item.keys()) {
                    comboProposer.addItem(name);
                }
            }
        }

        fireMarkEvaluatorChangedEvent();
    }

    private void fireMarkEvaluatorChangedEvent() {
        MarkEvaluatorChangedEvent event =
                new MarkEvaluatorChangedEvent(this, markEvaluatorSelected, getEvaluatorName());
        for (MarkEvaluatorChangedListener l :
                eventListenerList.getListeners(MarkEvaluatorChangedListener.class)) {
            l.markEvaluatorChanged(event);
        }
    }

    public synchronized void addMarkEvaluatorChangedListener(
            MarkEvaluatorChangedListener listener) {
        eventListenerList.add(MarkEvaluatorChangedListener.class, listener);
    }

    public synchronized void removeEventListener(MarkEvaluatorChangedListener listener) {
        eventListenerList.remove(MarkEvaluatorChangedListener.class, listener);
    }

    public ProposalOperationCreator createProposerEvaluator(String itemName)
            throws CreateException {

        String typeName = (String) comboType.getSelectedItem();

        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
            if (typeName.equals(item.getEvaluatorName())) {
                return item.createEvaluator(itemName);
            }
        }
        throw new AnchorImpossibleSituationException();
    }

    public JPanel getPanel() {
        return panel;
    }

    public Optional<ProposalOperationCreator> evaluator() {
        return evaluator;
    }

    public EvaluatorWithContextGetter evaluatorWithContext() {
        return () -> {
            if (!evaluator.isPresent() || markEvaluatorSelected == null) {
                return Optional.empty();
            }

            return Optional.of(
                    new EvaluatorWithContext(
                            evaluator.get(),
                            markEvaluatorSelected.getNRGStack(),
                            markEvaluatorSelected.getCfgGen(),
                            markEvaluatorSelected.getNrgScheme().getRegionMap()));
        };
    }
}
