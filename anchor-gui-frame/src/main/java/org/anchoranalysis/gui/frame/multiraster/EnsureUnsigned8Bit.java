/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.image.stack.DisplayStack;

// Ensure unsigned 8-bit
class EnsureUnsigned8Bit<E extends Exception>
        implements FunctionWithException<Integer, DisplayStack, E> {

    private FunctionWithException<Integer, DisplayStack, E> bridge;

    public EnsureUnsigned8Bit(FunctionWithException<Integer, DisplayStack, E> bridge) {
        super();
        this.bridge = bridge;
    }

    @Override
    public DisplayStack apply(Integer sourceObject) throws E {
        return bridge.apply(sourceObject);
    }
}
