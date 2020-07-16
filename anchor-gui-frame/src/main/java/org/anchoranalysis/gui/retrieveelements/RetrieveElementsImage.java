/* (C)2020 */
package org.anchoranalysis.gui.retrieveelements;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.generator.raster.MIPGenerator;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

// An interface that allows receiving elements from a module
// When a function returns NULL, that element doesn't exist
public class RetrieveElementsImage extends RetrieveElements {

    private DisplayStack stack;

    private DisplayStack slice;

    public RetrieveElementsImage() {}

    public DisplayStack getStack() {
        return stack;
    }

    public void setStack(DisplayStack stack) {
        this.stack = stack;
    }

    public DisplayStack getSlice() {
        return slice;
    }

    public void setSlice(DisplayStack slice) {
        this.slice = slice;
    }

    @Override
    public void addToPopUp(AddToExportSubMenu popUp) {

        if (getStack() != null) {

            final DisplayStack currentStack = getStack();

            CachedOperation<Stack, AnchorNeverOccursException> opCreateStack =
                    cachedOpFromDisplayStack(currentStack);

            try {
                popUp.addExportItemStackGenerator("selectedStack", "Stack", opCreateStack);
            } catch (OperationFailedException e) {
                assert false;
            }

            if (currentStack.getDimensions().getZ() > 1) {
                MIPGenerator generatorMIP = new MIPGenerator(true, "selectedStackMIP");

                OperationGenerator<Stack, Stack> generator =
                        new OperationGenerator<Stack, Stack>(generatorMIP);
                popUp.addExportItem(
                        generator,
                        opCreateStack,
                        "selectedStackMIP",
                        "MIP",
                        generator.createManifestDescription(),
                        1);
            }
        }

        if (getSlice() != null) {

            final DisplayStack currentSlice = getSlice();

            try {
                popUp.addExportItemStackGenerator(
                        "selectedSlice", "Slice", cachedOpFromDisplayStack(currentSlice));
            } catch (OperationFailedException e) {
                assert false;
            }
        }
    }

    private CachedOperation<Stack, AnchorNeverOccursException> cachedOpFromDisplayStack(
            DisplayStack stack) {
        return new WrapOperationAsCached<>(() -> stack.createImgStack(false));
    }
}
