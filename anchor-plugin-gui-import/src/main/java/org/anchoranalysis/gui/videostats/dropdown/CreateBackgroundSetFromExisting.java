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

package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.interactivebrowser.OperationInitParams;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateBackgroundSetFromExisting
        extends CachedOperationWithProgressReporter<BackgroundSet, BackgroundStackContainerException> {

    private final OperationWithProgressReporter<BackgroundSet, BackgroundStackContainerException>
            existingBackgroundSet;
    private OperationInitParams pso;

    @Override
    protected BackgroundSet execute(ProgressReporter progressReporter)
            throws BackgroundStackContainerException {

        try {
            BackgroundSet bsExisting = existingBackgroundSet.doOperation(progressReporter);
            
            MPPInitParams so = pso.doOperation();
            ImageInitParams soImage = so.getImage();

            return BackgroundSetFactory.createBackgroundSetFromExisting(
                    bsExisting,
                    new WrapStackAsTimeSequence(CreateCombinedStack.apply(soImage)),
                    progressReporter);

        } catch (CreateException e) {
            throw new BackgroundStackContainerException(e);
        }
    }
}
