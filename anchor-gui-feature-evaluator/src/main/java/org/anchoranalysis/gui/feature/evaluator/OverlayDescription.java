/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.name.value.ComparatorOrderByName;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.cfgnrgtable.TitleValueTableModel;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.anchoranalysis.image.extent.ImageResolution;

class OverlayDescription extends TitleValueTableModel implements IUpdatableSinglePair {

    private static final long serialVersionUID = -5093139154944903750L;

    public OverlayDescription() {}

    @Override
    public void updateSingle(final Overlay overlay, NRGStackWithParams raster) {
        clear();

        // If we have no mark matching the current id
        if (overlay == null) {
            fireTableDataChanged();
            return;
        }

        ImageResolution sr =
                raster.getDimensions() != null ? raster.getDimensions().getRes() : null;
        addOverlayDetails(overlay, "", sr);

        fireTableDataChanged();
    }

    @Override
    public void updatePair(final Pair<Overlay> pair, NRGStackWithParams raster) {

        clear();
        // If we have no mark matching the current id
        if (pair == null) {
            fireTableDataChanged();
            return;
        }

        addEntry(new SimpleTitleValue("Pair", pair.toString()));

        addOverlayDetails(pair.getSource(), "Source: ", raster.getDimensions().getRes());
        addOverlayDetails(pair.getDestination(), "Dest: ", raster.getDimensions().getRes());

        fireTableDataChanged();
    }

    private void addOverlayDetails(Overlay overlay, String titlePrefix, ImageResolution sr) {

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
