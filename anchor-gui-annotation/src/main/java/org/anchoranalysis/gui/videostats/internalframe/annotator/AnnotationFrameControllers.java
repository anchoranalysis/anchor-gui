/*-
 * #%L
 * anchor-gui-annotation
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
