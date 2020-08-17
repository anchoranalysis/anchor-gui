/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.frame.display;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class BoundOverlayedDisplayStack {

    private final DisplayStack background;
    private BoundColoredOverlayCollection overlay;
    private RegionExtracter regionExtracter;

    public BoundOverlayedDisplayStack(DisplayStack background) {
        super();
        this.background = background;
    }

    public BoundOverlayedDisplayStack(
            DisplayStack background, BoundColoredOverlayCollection overlay) {
        super();
        this.background = background;
        this.overlay = overlay;
    }

    public void initRegionExtracter() {
        if (overlay == null) {
            this.regionExtracter = background.createRegionExtracter();
        } else {
            RegionExtracter reBackground = background.createRegionExtracter();
            this.regionExtracter = new RegionExtracterFromOverlay(reBackground, overlay);
        }
    }

    public ImageDimensions dimensions() {
        return background.dimensions();
    }

    // Creates a new DisplayStack after imposing the overlay on the background
    public DisplayStack extractFullyOverlayed() throws OperationFailedException {
        RegionExtracter re = background.createRegionExtracter();
        return re.extractRegionFrom(new BoundingBox(background.dimensions()), 1.0);
    }

    public final int getNumberChannels() {
        return background.getNumberChannels();
    }

    public Optional<VoxelDataType> unconvertedDataType() {
        return background.unconvertedDataType();
    }

    public int getUnconvertedVoxelAt(int channelIndex, Point3i point) {
        return background.getUnconvertedVoxelAt(channelIndex, point);
    }

    public RegionExtracter createRegionExtracter() {
        initRegionExtracter();
        return regionExtracter;
    }

    public DisplayStack getBackground() {
        return background;
    }
}
