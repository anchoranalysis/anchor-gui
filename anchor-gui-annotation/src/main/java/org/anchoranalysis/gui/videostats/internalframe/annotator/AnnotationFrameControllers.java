/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.frame.overlays.IShowOverlays;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;

public class AnnotationFrameControllers {

    private ExtractOverlays extractOverlays;
    private IShowOverlays showOverlays;

    private ControllerPopupMenuWithBackground popup;
    private ControllerAction action;

    public AnnotationFrameControllers(
            ExtractOverlays extractOverlays,
            IShowOverlays showOverlays,
            ControllerPopupMenuWithBackground popup,
            ControllerAction action) {
        super();
        this.extractOverlays = extractOverlays;
        this.showOverlays = showOverlays;
        this.popup = popup;
        this.action = action;
    }

    public ExtractOverlays extractOverlays() {
        return extractOverlays;
    }

    public IShowOverlays showOverlays() {
        return showOverlays;
    }

    public ControllerPopupMenuWithBackground popup() {
        return popup;
    }

    public ControllerAction action() {
        return action;
    }
}
