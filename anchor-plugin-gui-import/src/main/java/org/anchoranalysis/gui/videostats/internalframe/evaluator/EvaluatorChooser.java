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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.ProposalOperationCreatorFromProposer;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;

public class EvaluatorChooser {

    @Getter private JPanel panel = new JPanel();

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

        comboMarkEvaluator.addActionListener(e -> selectMarkEvaluator());
        comboType.addActionListener(e -> populateComboProposer());
        comboProposer.addActionListener(
                e -> {
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
        } catch (OperationFailedException e1) {
            errorReporter.recordError(EvaluatorChooser.class, e1);
        }

        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
            try {
                MPPInitParams so = markEvaluatorSelected.getProposerSharedObjectsOperation().get();
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
                            markEvaluatorSelected.getEnergyStack(),
                            markEvaluatorSelected.getMarkFactory(),
                            markEvaluatorSelected.getEnergyScheme().getRegionMap()));
        };
    }
}
