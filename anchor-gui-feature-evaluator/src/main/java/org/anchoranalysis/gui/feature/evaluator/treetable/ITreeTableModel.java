/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import java.awt.event.MouseListener;
import javax.swing.JComponent;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.netbeans.swing.outline.Outline;

public interface ITreeTableModel extends IUpdatableSinglePair {

    void resizeColumns();

    @Override
    void updateSingle(Overlay overlay, NRGStackWithParams raster);

    void updatePair(Pair<Overlay> pair, NRGStackWithParams raster);

    // We should do this to all our tables
    void addMouseListenerToOutline(MouseListener ml);

    JComponent getComponent();

    Outline getOutline();
}
