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

package org.anchoranalysis.gui.feature.evaluator.singlepair;

import java.util.ArrayList;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.overlay.Overlay;

public class UpdatableSinglePairList implements UpdatableSinglePair {

    private ArrayList<UpdatableSinglePair> delegate = new ArrayList<>();

    public boolean add(UpdatableSinglePair arg0) {
        return delegate.add(arg0);
    }

    @Override
    public void updateSingle(Overlay overlay, EnergyStack raster) {

        for (UpdatableSinglePair iup : delegate) {
            iup.updateSingle(overlay, raster);
        }
    }

    @Override
    public void updatePair(IdentifiablePair<Overlay> pair, EnergyStack raster) {

        for (UpdatableSinglePair iup : delegate) {
            iup.updatePair(pair, raster);
        }
    }
}
