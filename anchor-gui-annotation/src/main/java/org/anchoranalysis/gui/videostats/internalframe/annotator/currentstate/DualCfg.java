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

import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;

@NoArgsConstructor
public class DualCfg implements IQueryAcceptedRejected {

    private MarkCollection cfgAccepted = new MarkCollection();
    private MarkCollection cfgRejected = new MarkCollection();

    public DualCfg(MarkCollection cfgAccepted, MarkCollection cfgRejected) {
        super();
        this.cfgAccepted = cfgAccepted;

        if (cfgRejected != null) {
            this.cfgRejected = cfgRejected;
        } else {
            // Replace null initialisation with an empty set
            // This is handle backwards compatiblility from serialized annotation files
            //   when cfgRejected was not present
            this.cfgRejected = new MarkCollection();
        }
    }

    public void addAll(boolean accepted, MarkCollection src) {
        if (accepted) {
            cfgAccepted.addAll(src);
        } else {
            cfgRejected.addAll(src);
        }
    }

    // Remove from either cfgAccepted or cfgRejected depending on where it can be found
    public void removeFromEither(Mark mark) {
        int index = cfgAccepted.indexOf(mark);
        if (index != -1) {
            cfgAccepted.remove(index);
        } else {

            index = cfgRejected.indexOf(mark);
            if (index != -1) {
                cfgRejected.remove(index);
            } else {
                assert false;
            }
        }
    }

    @Override
    public MarkCollection getCfgAccepted() {
        return cfgAccepted;
    }

    @Override
    public MarkCollection getCfgRejected() {
        return cfgRejected;
    }

    public DualCfg shallowCopy() {
        DualCfg out = new DualCfg();
        out.cfgAccepted = cfgAccepted.shallowCopy();
        out.cfgRejected = cfgRejected.shallowCopy();
        return out;
    }

    public void addAll(DualCfg in) {
        cfgAccepted.addAll(in.cfgAccepted);
        cfgRejected.addAll(in.cfgRejected);
    }
}
