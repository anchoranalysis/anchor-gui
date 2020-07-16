/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import javax.swing.JPanel;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.gui.cfgnrgtable.TablePanel;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;

class OverlayDescriptionPanel {

    private OverlayDescription markDescription = new OverlayDescription();
    private DescriptionTopPanel descriptionTopPanel = new DescriptionTopPanel();

    public void updateDescriptionTop(OverlayCollection overlays) {
        descriptionTopPanel.updateDescriptionTop(overlays);
    }

    public boolean isFrozen() {
        return descriptionTopPanel.isFrozen();
    }

    public IUpdatableSinglePair getMarkDescription() {
        return markDescription;
    }

    public JPanel getPanel() {
        return descriptionTopPanel.getPanel();
    }

    public JPanel createSummaryPanel() {
        TablePanel summaryPanel = new TablePanel("Summary", markDescription, true);
        summaryPanel.getTable().setTableHeader(null);
        return summaryPanel.getPanel();
    }
}
