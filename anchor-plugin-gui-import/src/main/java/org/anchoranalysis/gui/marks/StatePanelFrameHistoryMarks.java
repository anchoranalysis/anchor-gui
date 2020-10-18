/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.marks;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

public class StatePanelFrameHistoryMarks {

    private StatePanelFrameHistory<IndexableMarksWithEnergy> delegate;

    public StatePanelFrameHistoryMarks(String title, boolean includeFrameAdjusting) {
        super();
        this.delegate = new StatePanelFrameHistory<>(title, includeFrameAdjusting);
    }

    // This is separated from the constructor, so we can set up all the event handlers before
    // calling init, so an event
    //   can be triggered for the initial state
    public void init(
            int initialIndex,
            BoundedIndexContainer<IndexableMarksWithEnergy> selectedHistory,
            StatePanel<IndexableMarksWithEnergy> tablePanel,
            ErrorReporter errorReporter)
            throws InitException {
        this.delegate.init(initialIndex, selectedHistory, tablePanel, errorReporter);
    }

    public void setFrameSliderVisible(boolean visibility) {
        delegate.setFrameSliderVisible(visibility);
    }

    public ControllerSize controllerSize() {
        return delegate.controllerSize();
    }

    public IModuleCreatorDefaultState moduleCreator() {
        return delegate.moduleCreator();
    }
}
