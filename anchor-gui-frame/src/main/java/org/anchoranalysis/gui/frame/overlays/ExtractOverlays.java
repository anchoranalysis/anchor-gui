/* (C)2020 */
package org.anchoranalysis.gui.frame.overlays;

import org.anchoranalysis.gui.frame.display.overlay.GetOverlayCollection;
import org.anchoranalysis.image.extent.ImageDimensions;

public interface ExtractOverlays extends GetOverlayCollection {

    ImageDimensions getDimensions();
}
