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

package org.anchoranalysis.gui.feature.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import org.anchoranalysis.anchor.mpp.pair.IdentifiablePair;
import org.anchoranalysis.core.name.value.ComparatorOrderByName;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePair;
import org.anchoranalysis.gui.marks.table.TitleValueTableModel;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.OverlayProperties;

class OverlayDescription extends TitleValueTableModel implements UpdatableSinglePair {

    private static final long serialVersionUID = -5093139154944903750L;

    @Override
    public void updateSingle(final Overlay overlay, EnergyStack raster) {
        clear();

        // If we have no mark matching the current id
        if (overlay == null) {
            fireTableDataChanged();
            return;
        }

        Resolution sr = raster.dimensions() != null ? raster.dimensions().resolution() : null;
        addOverlayDetails(overlay, "", sr);

        fireTableDataChanged();
    }

    @Override
    public void updatePair(final IdentifiablePair<Overlay> pair, EnergyStack raster) {

        clear();
        // If we have no mark matching the current id
        if (pair == null) {
            fireTableDataChanged();
            return;
        }

        addEntry(new SimpleTitleValue("Pair", pair.toString()));

        addOverlayDetails(pair.getSource(), "Source: ", raster.dimensions().resolution());
        addOverlayDetails(pair.getDestination(), "Dest: ", raster.dimensions().resolution());

        fireTableDataChanged();
    }

    private void addOverlayDetails(Overlay overlay, String titlePrefix, Resolution sr) {

        OverlayProperties op = overlay.generateProperties(sr);

        ArrayList<NameValue<String>> listToAdd = new ArrayList<>();
        for (NameValue<String> nv : op) {
            listToAdd.add(nv);
        }

        Collections.sort(listToAdd, new ComparatorOrderByName<String>());

        for (NameValue<String> nv : listToAdd) {
            addEntry(new SimpleTitleValue(titlePrefix + nv.getName(), nv.getValue()));
        }
    }
}
