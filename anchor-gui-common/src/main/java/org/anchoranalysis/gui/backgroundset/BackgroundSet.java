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

package org.anchoranalysis.gui.backgroundset;

import java.util.HashMap;
import java.util.Set;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.container.background.CombineRGBBackgroundStackCntr;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor
public class BackgroundSet {

    private HashMap<
                    String,
                    BackgroundSetSupplier<BackgroundStackContainer>>
            map = new HashMap<>();

    public void addAll(BackgroundSet src) {
        for (String srcItem : src.map.keySet()) {
            addItem(srcItem, src.getItem(srcItem));
        }
    }

    public void addItem(String name, BackgroundStackContainer rasterBackground) {
        addItem(name, ()->rasterBackground);
    }

    public void addItem(String name, final Stack stack) throws OperationFailedException {
        addItem(name, BackgroundStackContainerFactory.singleSavedStack(stack));
    }

    public void addItem(
            String name,
            BackgroundSetSupplier<BackgroundStackContainer>
                    rasterBackground) {
        map.put(name, rasterBackground);
    }

    public BackgroundStackContainer getItem(String name) {
        try {
            return map.get(name).get();
        } catch (BackgroundStackContainerException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public DisplayStack singleStack(String name) throws BackgroundStackContainerException {

        try {
            BackgroundStackContainer finderRaster = map.get(name).get();

            if (finderRaster == null) {
                return null;
            }

            return finderRaster.container().get(0);
        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }

    // Gives us a stack container for a particular name, or NULL if none exists
    // NOTE: There is only a mapping between 0 and a single image
    public CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException>
            stackCntr(String name) throws GetOperationFailedException {
        try {

            BackgroundSetSupplier<BackgroundStackContainer> op = map.get(name);

            if (op == null) {
                return null;
            }

            BackgroundStackContainer backgroundStackCntr = op.get();

            if (backgroundStackCntr == null) {
                return null;
            }

            return new BoundedIndexBridge<>(backgroundStackCntr.container());
        } catch (BackgroundStackContainerException e) {
            throw new GetOperationFailedException(name, e);
        }
    }

    public Set<String> names() {
        return map.keySet();
    }

    public void addCombined(String top, String bottom) throws CreateException {
        CombineRGBBackgroundStackCntr combined = new CombineRGBBackgroundStackCntr();

        BackgroundStackContainer red = getItem(bottom);
        BackgroundStackContainer green = getItem(top);

        assert (red != null && green != null);

        combined.setRed(red);
        combined.setGreen(green);
        addItem(top + " on " + bottom, combined.create());
    }
}
