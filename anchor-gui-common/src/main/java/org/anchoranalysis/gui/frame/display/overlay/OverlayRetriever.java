package org.anchoranalysis.gui.frame.display.overlay;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.geometry.Point3i;

public interface OverlayRetriever extends IGetOverlayCollection {

	OverlayCollection overlaysAt( Point3i pnt);
}
