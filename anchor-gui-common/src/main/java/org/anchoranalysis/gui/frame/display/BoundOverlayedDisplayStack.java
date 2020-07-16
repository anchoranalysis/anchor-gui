/* (C)2020 */
package org.anchoranalysis.gui.frame.display;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class BoundOverlayedDisplayStack {

    private DisplayStack background;
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

    public ImageDimensions getDimensions() {
        return background.getDimensions();
    }

    // Creates a new DisplayStack after imposing the overlay on the background
    public DisplayStack extractFullyOverlayed() throws OperationFailedException {
        RegionExtracter re = background.createRegionExtracter();
        return re.extractRegionFrom(new BoundingBox(background.getDimensions().getExtent()), 1.0);
    }

    public final int getNumChnl() {
        return background.getNumChnl();
    }

    public Optional<VoxelDataType> unconvertedDataType() {
        return background.unconvertedDataType();
    }

    public int getUnconvertedVoxelAt(int c, int x, int y, int z) {
        return background.getUnconvertedVoxelAt(c, x, y, z);
    }

    public RegionExtracter createRegionExtracter() {
        initRegionExtracter();
        return regionExtracter;
    }

    public DisplayStack getBackground() {
        return background;
    }
}
