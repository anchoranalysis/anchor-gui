/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

class NoOverlayBridgeFromGenerator
        implements FunctionWithException<Integer, DisplayUpdate, OperationFailedException> {

    private IterableObjectGenerator<Integer, DisplayStack> generator;

    public NoOverlayBridgeFromGenerator(IterableObjectGenerator<Integer, DisplayStack> generator) {
        super();
        this.generator = generator;
    }

    @Override
    public DisplayUpdate apply(Integer sourceObject) throws OperationFailedException {
        try {
            generator.setIterableElement(sourceObject);
            BoundOverlayedDisplayStack overlayedStack =
                    new BoundOverlayedDisplayStack(generator.getGenerator().generate());
            return DisplayUpdate.assignNewStack(overlayedStack);

        } catch (SetOperationFailedException | OutputWriteFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
