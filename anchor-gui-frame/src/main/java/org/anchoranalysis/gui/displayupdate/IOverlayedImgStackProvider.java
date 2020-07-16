/* (C)2020 */
package org.anchoranalysis.gui.displayupdate;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;

public interface IOverlayedImgStackProvider {

    BoundOverlayedDisplayStack getCurrentDisplayStack() throws GetOperationFailedException;
}
