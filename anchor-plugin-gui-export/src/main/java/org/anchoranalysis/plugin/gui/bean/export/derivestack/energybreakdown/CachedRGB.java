/*-
 * #%L
 * anchor-plugin-gui-export
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

package org.anchoranalysis.plugin.gui.bean.export.derivestack.energybreakdown;

import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

class CachedRGB {

    // Start - these parameters never change
    private final IdentifierGetter<Overlay> idGetter;

    private DisplayStack backgroundOriginal;

    private RGBStack rgb;

    private ColoredOverlayCollection currentOverlays;

    private DrawOverlay drawOverlay;

    private boolean needsBackgroundRefresh = false;

    public CachedRGB(final IdentifierGetter<Overlay> idGetter) {

        this.idGetter = idGetter;

        this.currentOverlays = new ColoredOverlayCollection();

        // We have no background yet, setBackground must be called before we start
        needsBackgroundRefresh = true;
    }

    public void updateDrawer(DrawOverlay drawOverlay) {
        // change to only trigger a redraw at the next operation
        this.drawOverlay = drawOverlay;
        needsBackgroundRefresh = true;
    }

    // resets it all to the orginal, but doesn't draw any overlays
    private void resetToOriginalChannelAll() {

        createRGBFromChannel(backgroundOriginal);

        needsBackgroundRefresh = false;
    }

    // When we replace the current overlays with new overlays
    public void updateMarks(OverlayedDisplayStackUpdate update) throws OperationFailedException {

        if (update == null) {
            return;
        }

        if (update.getBackgroundStack() != null) {
            setBackground(update.getBackgroundStack());
        }

        // We do the whole image
        if (update.getRedrawParts() == null) {

            List<BoundingBox> boxListReset =
                    currentOverlays.boxList(drawOverlay, backgroundOriginal.dimensions());

            if (update.getColoredMarks() != null) {
                ColoredOverlayCollection overlaysNew = update.getColoredMarks();
                resetToChannelOrig(boxListReset);
                drawMarks(overlaysNew);
                this.currentOverlays = overlaysNew;

            } else {
                resetToChannelOrig(boxListReset);
                drawMarks(currentOverlays);
            }

        } else {
            List<BoundingBox> listBBox =
                    update.getRedrawParts().boxList(drawOverlay, backgroundOriginal.dimensions());

            if (update.getColoredMarks() != null) {
                ColoredOverlayCollection overlaysNew = update.getColoredMarks();
                resetToChannelOrig(listBBox);
                drawMarksIfIntersects(overlaysNew, listBBox);
                this.currentOverlays = overlaysNew;

            } else {
                resetToChannelOrig(listBBox);
                drawMarksIfIntersects(currentOverlays, listBBox);
            }
        }
    }

    // START we will remove these

    // Allows us to change just the background
    private void setBackground(DisplayStack background) {
        if (background != this.backgroundOriginal) {
            this.backgroundOriginal = background;
            needsBackgroundRefresh = true;
        }
    }

    // STOP - we will remove these

    private void resetToChannelOrig(List<BoundingBox> listBBox) {

        if (needsBackgroundRefresh) {
            resetToOriginalChannelAll();
            return;
        }

        if (listBBox == null) {
            resetToOriginalChannelAll();
        }

        for (BoundingBox box : listBBox) {

            BoundingBox boxClipped = box.clipTo(backgroundOriginal.extent());

            for (int c = 0; c < 3; c++) {
                Channel rgbTarget = rgb.channelAt(c);

                Voxels<UnsignedByteBuffer> voxelsTarget = rgbTarget.voxels().asByte();

                int bgChannel = selectBackgroundChannel(c, backgroundOriginal.getNumberChannels());
                backgroundOriginal.copyPixelsTo(bgChannel, boxClipped, voxelsTarget, boxClipped);
            }
        }
    }

    private static int selectBackgroundChannel(int iter, int numChannelsInBackground) {
        // If backgroundOrig is single channel always take from 0
        if (numChannelsInBackground == 1) {
            return 0;
        } else if (numChannelsInBackground == 3) {
            return iter;
        } else {
            assert false;
            return 0;
        }
    }

    private void drawMarks(ColoredOverlayCollection overlays) throws OperationFailedException {
        assert (overlays.getColorList() != null);
        assert (overlays.getColorList().size() == overlays.size());
        // TODO We only draw overlays which intersect with the bounding box
        drawOverlay.writeOverlays(overlays, rgb, idGetter);
    }

    private void drawMarksIfIntersects(ColoredOverlayCollection oc, List<BoundingBox> boxList)
            throws OperationFailedException {

        // We only draw overlays which intersect with the bounding box
        drawOverlay.writeOverlaysIfIntersects(oc, rgb, idGetter, boxList);
    }

    private void createRGBFromChannel(DisplayStack background) {
        rgb = ConvertDisplayStackToRGB.convert(background);
    }

    public RGBStack getRGB() {
        if (rgb == null) {
            resetToOriginalChannelAll();
        }
        return rgb;
    }

    public ColoredOverlayCollection getColoredMarks() {
        return currentOverlays;
    }

    public DisplayStack getBackground() {
        return backgroundOriginal;
    }
}
