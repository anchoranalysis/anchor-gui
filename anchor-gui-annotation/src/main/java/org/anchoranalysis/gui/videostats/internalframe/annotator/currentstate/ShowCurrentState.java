/*-
 * #%L
 * anchor-gui-annotation
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
