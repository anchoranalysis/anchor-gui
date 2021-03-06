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

package org.anchoranalysis.gui.annotation;

import lombok.Getter;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.core.progress.ProgressOneOfMany;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFactory;
import org.anchoranalysis.gui.videostats.link.DefaultLinkStateManager;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;

public class AnnotationBackgroundInstance {

    @Getter private BackgroundSetProgressingSupplier backgroundSetOp;

    @Getter
    private CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException>
            defaultBackground;

    @Getter private Dimensions dimensionsViewer;

    public AnnotationBackgroundInstance(
            ProgressMultiple progressMultiple,
            NamedProvider<Stack> backgroundStacks,
            String stackNameVisualOriginal)
            throws BackgroundStackContainerException {
        backgroundSetOp = CreateBackgroundSetFactory.createCached(backgroundStacks);
        try {
            defaultBackground =
                    backgroundSetOp
                            .get(new ProgressOneOfMany(progressMultiple))
                            .stackCntr(stackNameVisualOriginal);

            if (defaultBackground == null) {
                throw new BackgroundStackContainerException(
                        String.format("Cannot find stackName '%s'", stackNameVisualOriginal));
            }

            dimensionsViewer = defaultBackground.apply(0).dimensions();
        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }

    public void configureLinkManager(DefaultLinkStateManager linkStateManager) {
        linkStateManager.setBackground(defaultBackground);
        linkStateManager.setSliceNum(dimensionsViewer.z() / 2);
    }
}
