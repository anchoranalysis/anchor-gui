/* (C)2020 */
package org.anchoranalysis.gui.frame.display.overlay;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.geometry.Point3i;

public interface OverlayRetriever extends GetOverlayCollection {

    OverlayCollection overlaysAt(Point3i point);
}
