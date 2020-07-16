/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.singlepair;

import java.util.ArrayList;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class UpdatableSinglePairList implements IUpdatableSinglePair {

    private ArrayList<IUpdatableSinglePair> delegate = new ArrayList<>();

    public boolean add(IUpdatableSinglePair arg0) {
        return delegate.add(arg0);
    }

    @Override
    public void updateSingle(Overlay overlay, NRGStackWithParams raster) {

        for (IUpdatableSinglePair iup : delegate) {
            iup.updateSingle(overlay, raster);
        }
    }

    @Override
    public void updatePair(Pair<Overlay> pair, NRGStackWithParams raster) {

        for (IUpdatableSinglePair iup : delegate) {
            iup.updatePair(pair, raster);
        }
    }
}
