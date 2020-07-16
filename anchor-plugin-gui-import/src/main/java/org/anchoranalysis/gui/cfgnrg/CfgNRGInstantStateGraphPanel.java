/* (C)2020 */
package org.anchoranalysis.gui.cfgnrg;

import java.util.Optional;
import javax.swing.JPanel;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.gui.plot.panel.GraphPanel;

public class CfgNRGInstantStateGraphPanel extends StatePanel<CfgNRGInstantState> {

    private GraphPanel graphPanel;

    private FunctionWithException<
                    CfgNRGInstantState, ClickableGraphInstance, OperationFailedException>
            graphGenerator;

    public CfgNRGInstantStateGraphPanel(
            FunctionWithException<
                            CfgNRGInstantState, ClickableGraphInstance, OperationFailedException>
                    graphGenerator) {
        this.graphGenerator = graphGenerator;
    }

    @Override
    public JPanel getPanel() {
        return graphPanel.getPanel();
    }

    @Override
    public void updateState(CfgNRGInstantState state) throws StatePanelUpdateException {

        try {
            ClickableGraphInstance graphInstance = graphGenerator.apply(state);

            if (graphPanel == null) {
                this.graphPanel = new GraphPanel(graphInstance);
            } else {
                this.graphPanel.updateGraph(graphInstance);
            }
        } catch (OperationFailedException e) {
            throw new StatePanelUpdateException(e);
        }
    }

    @Override
    public Optional<IPropertyValueSendable<IntArray>> getSelectMarksSendable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<IntArray>> getSelectMarksReceivable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<OverlayCollection>>
            getSelectOverlayCollectionReceivable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<Integer>> getSelectIndexReceivable() {
        return Optional.empty();
    }
}
