/* (C)2020 */
package org.anchoranalysis.gui.container.background;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.image.stack.DisplayStack;

public class SingleBackgroundStackCntr implements BackgroundStackCntr {

    private BoundedIndexContainer<DisplayStack> cntr;

    public SingleBackgroundStackCntr(BoundedIndexContainer<DisplayStack> cntr) {
        super();
        this.cntr = cntr;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public BoundedIndexContainer<DisplayStack> backgroundStackCntr()
            throws GetOperationFailedException {
        return cntr;
    }
}
