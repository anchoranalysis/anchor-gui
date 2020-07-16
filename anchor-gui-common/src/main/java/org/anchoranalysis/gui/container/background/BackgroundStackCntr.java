/* (C)2020 */
package org.anchoranalysis.gui.container.background;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.image.stack.DisplayStack;

public interface BackgroundStackCntr {

    boolean exists();

    BoundedIndexContainer<DisplayStack> backgroundStackCntr() throws GetOperationFailedException;
}
