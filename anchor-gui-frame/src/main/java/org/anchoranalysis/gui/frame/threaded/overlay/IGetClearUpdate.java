/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.overlay;

import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;

interface IGetClearUpdate {

    void clearWaitingUpdate();

    OverlayedDisplayStackUpdate getAndClearWaitingUpdate();
}
