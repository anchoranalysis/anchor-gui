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

package org.anchoranalysis.gui.retrieveelements;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.AnchorNeverOccursException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.generator.raster.FlattenStackGenerator;

// An interface that allows receiving elements from a module
// When a function returns null, that element doesn't exist
@AllArgsConstructor
public class RetrieveElementsImage extends RetrieveElements {

    private Optional<DisplayStack> stack;
    private Optional<DisplayStack> slice;

    @Override
    public void addToPopUp(AddToExportSubMenu popUp) {

        stack.ifPresent(raster -> addFromStack(popUp, raster));

        slice.ifPresent(raster -> addFromSlice(popUp, raster));
    }

    private void addFromStack(AddToExportSubMenu popUp, DisplayStack stack) {
        Supplier<Stack> opCreateStack = cachedOpFromDisplayStack(stack);

        try {
            popUp.addExportItemStackGenerator("selectedStack", "Stack", opCreateStack);
        } catch (OperationFailedException e) {
            assert false;
        }

        if (stack.dimensions().z() > 1) {
            FlattenStackGenerator generatorMIP =
                    new FlattenStackGenerator(true, "selectedStackMIP");

            SupplierGenerator<Stack, Stack> generator = new SupplierGenerator<>(generatorMIP);
            popUp.addExportItem(
                    generator,
                    opCreateStack,
                    "selectedStackMIP",
                    "MIP",
                    generator.createManifestDescription(),
                    1);
        }
    }

    private void addFromSlice(AddToExportSubMenu popUp, DisplayStack slice) {
        try {
            popUp.addExportItemStackGenerator(
                    "selectedSlice", "Slice", cachedOpFromDisplayStack(slice));
        } catch (OperationFailedException e) {
            assert false;
        }
    }

    private Supplier<Stack> cachedOpFromDisplayStack(DisplayStack stack) {
        CachedSupplier<Stack, AnchorNeverOccursException> cachedSupplier =
                CachedSupplier.cache(() -> stack.deriveStack(false));
        return cachedSupplier::get;
    }
}
