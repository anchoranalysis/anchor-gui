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

package org.anchoranalysis.gui.marks;

import java.util.Optional;
import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.plot.panel.ClickablePlotInstance;
import org.anchoranalysis.gui.plot.panel.GraphPanel;
import org.anchoranalysis.gui.videostats.link.IntArray;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.overlay.collection.OverlayCollection;

@RequiredArgsConstructor
public class MarksGraphPanel extends StatePanel<IndexableMarksWithEnergy> {

    // START REQUIRED ARGUMENTS
    private final CheckedFunction<
                    IndexableMarksWithEnergy, ClickablePlotInstance, OperationFailedException>
            graphGenerator;
    // END REQUIRED ARGUMENTS

    private GraphPanel graphPanel;

    @Override
    public JPanel getPanel() {
        return graphPanel.getPanel();
    }

    @Override
    public void updateState(IndexableMarksWithEnergy state) throws StatePanelUpdateException {

        try {
            ClickablePlotInstance graphInstance = graphGenerator.apply(state);

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
