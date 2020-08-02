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

package org.anchoranalysis.gui.frame.multiraster;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.image.stack.DisplayStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ConvertToDisplayStack {

    public static DisplayStack apply(NamedRasterSet set) throws BackgroundStackContainerException {
        return extractAtIndex(convertBackgroundSet(backgroundFromSet(set)));
    }

    private static BackgroundSet backgroundFromSet(NamedRasterSet set)
            throws BackgroundStackContainerException {
        return set.getBackgroundSet().call(ProgressReporterNull.get());
    }

    private static BoundedIndexContainer<DisplayStack> convertBackgroundSet(BackgroundSet bg)
            throws BackgroundStackContainerException {
        return bg.getItem(
                        bg.names().iterator().next() // Arbitrary name
                        )
                .container();
    }

    private static DisplayStack extractAtIndex(BoundedIndexContainer<DisplayStack> indexCntr)
            throws BackgroundStackContainerException {
        if (indexCntr.getMinimumIndex() != indexCntr.getMaximumIndex()) {
            throw new BackgroundStackContainerException("BackgroundSet has more than one image");
        }
        try {
            return indexCntr.get(indexCntr.getMinimumIndex());
        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }
}
