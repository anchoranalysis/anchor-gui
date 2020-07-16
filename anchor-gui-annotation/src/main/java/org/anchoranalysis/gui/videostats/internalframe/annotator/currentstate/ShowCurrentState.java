/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.gui.frame.overlays.IShowOverlays;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

public class ShowCurrentState {

    private IShowOverlays showResult;
    private IShowError showError;

    public ShowCurrentState(IShowOverlays showResult, IShowError showError) {
        super();
        this.showResult = showResult;
        this.showError = showError;
    }

    public void showAtSlice(CurrentState currentState, int z) {
        showResult.showOverlays(
                RedrawUpdateFromProposal.apply(
                        cfg(currentState, true, z), currentState.getRefreshListAndReset()));
        showError.clearErrors();
    }

    public void show(CurrentState currentState) {
        showResult.showOverlays(
                RedrawUpdateFromProposal.apply(
                        cfg(currentState, false), currentState.getRefreshListAndReset()));
        showError.clearErrors();
    }

    public void showRedrawAll(CurrentState currentState) {
        showResult.showOverlays(RedrawUpdateFromProposal.apply(cfg(currentState, false), null));
        showError.clearErrors();
    }

    public void showError(String message) {
        showError.showError(message);
    }

    private ProposedCfg cfg(CurrentState currentState, boolean success, int suggestedSliceNum) {
        ProposedCfg plainCfg = cfg(currentState, success);
        plainCfg.setSuggestedSliceNum(suggestedSliceNum);
        return plainCfg;
    }

    private ProposedCfg cfg(CurrentState currentState, boolean success) {
        ProposedCfg plainCfg = new ProposedCfg();
        plainCfg.setSuccess(success);
        plainCfg.setColoredCfg(currentState.generateFullCfg());
        return plainCfg;
    }
}
