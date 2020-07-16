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
/* (C)2020 */
package org.anchoranalysis.gui.image;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class OverlayCollectionWithImgStack {

    private OverlayCollection overlayCollection;
    private NRGStackWithParams stack;

    public OverlayCollectionWithImgStack(
            OverlayCollection overlayCollection, NRGStackWithParams stack) {
        super();
        this.overlayCollection = overlayCollection;
        this.stack = stack;
    }

    public OverlayCollection getOverlayCollection() {
        return overlayCollection;
    }

    public NRGStackWithParams getStack() {
        return stack;
    }

    public void setStack(NRGStackWithParams stack) {
        this.stack = stack;
    }

    public void setOverlayCollection(OverlayCollection overlayCollection) {
        this.overlayCollection = overlayCollection;
    }

    public OverlayCollectionWithImgStack copyChangeStack(NRGStackWithParams stack) {
        return new OverlayCollectionWithImgStack(overlayCollection, stack);
    }
}
