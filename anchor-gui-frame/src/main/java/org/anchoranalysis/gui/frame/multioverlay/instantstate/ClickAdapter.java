/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.image.ISliceNumGetter;
import org.anchoranalysis.gui.indices.DualIndicesSelection;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.anchoranalysis.spatial.point.Point3i;

class ClickAdapter extends MouseAdapter {

    private ISliceNumGetter sliceNumGetter;

    private OverlayRetriever overlaysGetter;

    private DualIndicesSelection selectionIndices;
    private PropertyValueChangeListenerList<OverlayCollection> eventListenerList =
            new PropertyValueChangeListenerList<>();

    public ClickAdapter(
            DualIndicesSelection selectionIndices,
            ISliceNumGetter sliceNumGetter,
            OverlayRetriever overlaysGetter) {
        super();
        this.selectionIndices = selectionIndices;
        this.sliceNumGetter = sliceNumGetter;
        this.overlaysGetter = overlaysGetter;
    }

    @Override
    public void mousePressed(MouseEvent event) {

        // If any control keys are also pressed, or if we happen to be triggering the pop, we ignore
        if (event.isPopupTrigger()
                || event.isShiftDown()
                || event.isMetaDown()
                || event.isAltDown()
                || event.isAltGraphDown()) {
            return;
        }

        // If it's not the left mouse button, we ignore
        if (!SwingUtilities.isLeftMouseButton(event)) {
            return;
        }

        Point3i point = new Point3i(event.getX(), event.getY(), sliceNumGetter.getSliceNum());

        // This our current
        OverlayCollection selectedOverlays = overlaysGetter.overlaysAt(point);

        int[] ids = idArrayFromOverlayCollection(selectedOverlays);
        if (event.isControlDown()) {
            // If control is pressed, we add/remove objects from selection

            // Then we add all the exisitng ids to the selectionIndices
            ids =
                    IndicesSelection.mergedList(
                            ids, selectionIndices.getCurrentSelection().getCurrentSelection());
            // Then we merge the two lists.  If an ID appears twice, then we remove it
        } else {
            // If control is not pressed, we select new objects
            if (selectionIndices.getCurrentSelection().equalsInt(ids)) {
                // If we click on a mark that is already toggled, then we reset to null
                ids = new int[] {};
            }
        }

        selectionIndices.updateBoth(ids);

        ColoredOverlayCollection overlays = overlaysGetter.getOverlays();

        OverlayCollection overlaysSubset =
                overlays.getOverlays().createSubset(new IndicesSelection(ids));

        // We also trigger an selectMark
        triggerObjectChangeEvent(overlaysSubset);
    }

    private static int[] idArrayFromOverlayCollection(OverlayCollection oc) {
        int[] out = new int[oc.size()];
        for (int i = 0; i < oc.size(); i++) {
            out[i] = oc.get(i).getId();
        }
        return out;
    }

    private void triggerObjectChangeEvent(OverlayCollection state) {
        for (PropertyValueChangeListener<OverlayCollection> listener : eventListenerList) {
            listener.propertyValueChanged(new PropertyValueChangeEvent<>(this, state, false));
        }
    }

    public IPropertyValueReceivable<OverlayCollection> createSelectOverlayCollectionReceivable() {
        return eventListenerList.createPropertyValueReceivable();
    }
}
