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
import java.util.Optional;
import org.anchoranalysis.core.identifier.name.ComparatorOrderByName;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePair;
import org.anchoranalysis.gui.marks.table.TitleValueTableModel;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
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

        addOverlayDetails(overlay, "", raster.dimensions().resolution());

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

        addOverlayDetails(pair.getSource(), "Source: ", raster.resolution());
        addOverlayDetails(pair.getDestination(), "Dest: ", raster.resolution());

        fireTableDataChanged();
    }

    private void addOverlayDetails(
            Overlay overlay, String titlePrefix, Optional<Resolution> resolution) {

        OverlayProperties properties = overlay.generateProperties(resolution);

        ArrayList<NameValue<String>> listToAdd = new ArrayList<>();
        for (NameValue<String> nameValue : properties) {
            listToAdd.add(nameValue);
        }

        Collections.sort(listToAdd, new ComparatorOrderByName<String>());

        for (NameValue<String> nameValue : listToAdd) {
            addEntry(new SimpleTitleValue(titlePrefix + nameValue.getName(), nameValue.getValue()));
        }
    }
}
