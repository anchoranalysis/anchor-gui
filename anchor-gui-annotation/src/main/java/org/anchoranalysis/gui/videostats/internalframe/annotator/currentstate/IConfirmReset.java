/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ConfirmResetStateChangedListener;

public interface IConfirmReset extends IAcceptProposal {

    public boolean canConfirm();

    public boolean canReset();

    void reset();

    void addConfirmResetStateChangedListener(ConfirmResetStateChangedListener e);
}
