/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;

public abstract class ControllerZoom {

    public abstract void setEnforceMinimumSizeAfterGuessZoom(
            boolean enforceMinimumSizeAfterGuessZoom);

    public abstract void setDefaultZoomSuggestor(DefaultZoomSuggestor defaultZoomSuggestor);
}
