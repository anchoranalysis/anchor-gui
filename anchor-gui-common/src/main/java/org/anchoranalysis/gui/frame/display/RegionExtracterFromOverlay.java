/* (C)2020 */
package org.anchoranalysis.gui.frame.display;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.stack.rgb.RGBStack;

class RegionExtracterFromOverlay implements RegionExtracter {

    private RegionExtracter regionExtracter;
    private BoundColoredOverlayCollection overlay;

    public RegionExtracterFromOverlay(
            RegionExtracter regionExtracter, BoundColoredOverlayCollection overlay) {
        super();
        this.regionExtracter = regionExtracter;
        this.overlay = overlay;
    }

    @Override
    public DisplayStack extractRegionFrom(BoundingBox bbox, double zoomFactor)
            throws OperationFailedException {
        DisplayStack ds = regionExtracter.extractRegionFrom(bbox, zoomFactor);

        try {
            RGBStack rgbStack = ConvertDisplayStackToRGB.convert(ds);
            overlay.drawRGB(rgbStack, bbox, zoomFactor);
            return DisplayStack.create(rgbStack);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
