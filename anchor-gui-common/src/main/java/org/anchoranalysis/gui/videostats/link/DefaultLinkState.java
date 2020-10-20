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

package org.anchoranalysis.gui.videostats.link;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.image.OverlaysWithEnergyStack;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.apache.commons.lang.ArrayUtils;

public class DefaultLinkState {

    @Getter @Setter private int frameIndex;

    @Getter @Setter private int sliceNum;

    @Getter @Setter private int[] objectIDs = new int[] {};

    @Getter @Setter private OverlayCollection overlayCollection;

    @Getter @Setter private OverlaysWithEnergyStack overlaysWithStack;

    @Getter @Setter
    private CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> background;

    DefaultLinkState duplicate() {
        DefaultLinkState dms = new DefaultLinkState();
        dms.frameIndex = frameIndex;
        dms.sliceNum = sliceNum;
        dms.objectIDs = ArrayUtils.clone(objectIDs);
        dms.overlayCollection = overlayCollection;
        dms.overlaysWithStack = overlaysWithStack;
        dms.background = background;
        return dms;
    }
}
