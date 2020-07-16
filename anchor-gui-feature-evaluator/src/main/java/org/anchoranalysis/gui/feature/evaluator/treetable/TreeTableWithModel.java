/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import java.awt.event.MouseListener;
import javax.swing.JComponent;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.FeatureCalcDescriptionTreeModel;
import org.netbeans.swing.outline.Outline;

// Has a number of different tables for cliques sizes (0, 1, 2) all of which remain memory, but only
// one
//   of which is visible at any given time
public class TreeTableWithModel implements ITreeTableModel {

    private TreeTable delegate;
    private FeatureCalcDescriptionTreeModel featureTree;

    public TreeTableWithModel(
            TreeTableProperties properties,
            FeatureListWithRegionMap<FeatureInput> featureList,
            SharedFeatureMulti sharedFeatures) {
        super();
        this.featureTree =
                new FeatureCalcDescriptionTreeModel(
                        featureList, sharedFeatures, properties.getLogErrorReporter());
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
    public void updateSingle(Overlay overlay, NRGStackWithParams raster) {
        featureTree.updateSingle(overlay, raster);
    }

    @Override
    public void updatePair(Pair<Overlay> pair, NRGStackWithParams raster) {
        featureTree.updatePair(pair, raster);
    }

    // We should do this to all our tables
    @Override
    public void addMouseListenerToOutline(MouseListener ml) {
        delegate.getOutline().addMouseListener(ml);
    }
}
