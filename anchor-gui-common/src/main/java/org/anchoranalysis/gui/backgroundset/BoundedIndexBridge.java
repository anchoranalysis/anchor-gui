package org.anchoranalysis.gui.backgroundset;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;

@AllArgsConstructor
class BoundedIndexBridge<T>
        implements CheckedFunction<Integer, T, BackgroundStackContainerException> {

    /** The container associated with the bridge */
    @Setter private BoundedIndexContainer<T> container;

    @Override
    public T apply(Integer sourceObject) throws BackgroundStackContainerException {
        int index = container.previousEqualIndex(sourceObject);
        if (index == -1) {
            throw new BackgroundStackContainerException(
                    "Cannot find a previousEqualIndex in the cntr");
        }
        try {
            return container.get(index);
        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }
}
