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
import org.anchoranalysis.annotation.mark.DualMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * A marks-collection partitioned into accepted and rejected collections
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class PartitionedMarks implements DualMarks {

    private MarkCollection marksAccepted = new MarkCollection();
    private MarkCollection marksRejected = new MarkCollection();

    public PartitionedMarks(MarkCollection marksAccepted, MarkCollection marksRejected) {
        super();
        this.marksAccepted = marksAccepted;

        if (marksRejected != null) {
            this.marksRejected = marksRejected;
        } else {
            // Replace null initialisation with an empty set
            // This is handle backwards compatiblility from serialized annotation files
            //   when marksRejected was not present
            this.marksRejected = new MarkCollection();
        }
    }

    public void addAll(boolean accepted, MarkCollection src) {
        if (accepted) {
            marksAccepted.addAll(src);
        } else {
            marksRejected.addAll(src);
        }
    }

    // Remove from either marksAccepted or marksRejected depending on where it can be found
    public void removeFromEither(Mark mark) {
        int index = marksAccepted.indexOf(mark);
        if (index != -1) {
            marksAccepted.remove(index);
        } else {

            index = marksRejected.indexOf(mark);
            if (index != -1) {
                marksRejected.remove(index);
            } else {
                assert false;
            }
        }
    }

    @Override
    public MarkCollection accepted() {
        return marksAccepted;
    }

    @Override
    public MarkCollection rejected() {
        return marksRejected;
    }

    public PartitionedMarks shallowCopy() {
        PartitionedMarks out = new PartitionedMarks();
        out.marksAccepted = marksAccepted.shallowCopy();
        out.marksRejected = marksRejected.shallowCopy();
        return out;
    }

    public void addAll(PartitionedMarks in) {
        marksAccepted.addAll(in.marksAccepted);
        marksRejected.addAll(in.marksRejected);
    }
}
