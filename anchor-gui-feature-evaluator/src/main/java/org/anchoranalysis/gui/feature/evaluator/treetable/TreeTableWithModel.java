/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator.treetable;

import java.awt.event.MouseListener;
import javax.swing.JComponent;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.energytree.FeatureTreeModel;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.overlay.Overlay;
import org.netbeans.swing.outline.Outline;

// Has a number of different tables for cliques sizes (0, 1, 2) all of which remain memory, but only
// one
//   of which is visible at any given time
public class TreeTableWithModel implements ITreeTableModel {

    private TreeTable delegate;
    private FeatureTreeModel featureTree;

    public TreeTableWithModel(
            TreeTableProperties properties,
            FeatureListWithRegionMap<FeatureInput> featureList,
            SharedFeatureMulti sharedFeatures) {
        super();
        this.featureTree =
                new FeatureTreeModel(featureList, sharedFeatures, properties.getLogErrorReporter());
        this.delegate = new TreeTable(featureTree, properties);
    }

    @Override
    public void resizeColumns() {
        delegate.resizeColumns();
    }

    @Override
    public JComponent getComponent() {
        return delegate.getComponent();
    }

    public Outline getOutline() {
        return delegate.getOutline();
    }

    @Override
    public void updateSingle(Overlay overlay, EnergyStack raster) {
        featureTree.updateSingle(overlay, raster);
    }

    @Override
    public void updatePair(IdentifiablePair<Overlay> pair, EnergyStack raster) {
        featureTree.updatePair(pair, raster);
    }

    // We should do this to all our tables
    @Override
    public void addMouseListenerToOutline(MouseListener ml) {
        delegate.getOutline().addMouseListener(ml);
    }
}
