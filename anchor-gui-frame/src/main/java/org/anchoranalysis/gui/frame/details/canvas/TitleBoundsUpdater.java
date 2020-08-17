/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.details.canvas;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.gui.displayupdate.ProvidesOverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.gui.reassign.FrameTitleCreator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class TitleBoundsUpdater implements ChangeListener {
    
    private ErrorReporter errorReporter;
    private IndexGettableSettable indexCntr;
    private ProvidesOverlayedDisplayStack stackProvider;
    private SliceIndexSlider slider;
    private InternalFrameDelegate frame;
    private String frameName;

    @Override
    public void stateChanged(ChangeEvent e) {

        // Just in case our slice bounds change as the provided image changes
        try {
            updateSliceBounds();
        } catch (OperationFailedException e1) {
            errorReporter.recordError(TitleBoundsUpdater.class, e1);
        }
        updateTitle();
    }

    // Maybe this gets called before init
    public void updateSliceBounds() throws OperationFailedException {
        BoundOverlayedDisplayStack initialStack = this.stackProvider.getCurrentDisplayStack();
        ChnlSliceRange sliceBounds = new ChnlSliceRange(initialStack.dimensions());

        if (slider == null) {
            return;
        }

        slider.setSliceBounds(sliceBounds);
    }

    private String title(int iter) {
        if (slider.getIndexSliderVisible()) {
            return FrameTitleCreator.title(this.frameName, iter);
        } else {
            return FrameTitleCreator.title(this.frameName);
        }
    }

    public void updateTitle() {
        frame.setTitle( title(indexCntr.getIndex()) );
    }
}
