/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.kernel.match;

import java.util.ArrayList;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelAnd extends MatchKernel {

    private ArrayList<MatchKernel> conditions = new ArrayList<>();

    public MatchKernelAnd() {}

    public MatchKernelAnd(MatchKernel condition1, MatchKernel condition2) {
        conditions.add(condition1);
        conditions.add(condition2);
    }

    public void addCondition(MatchKernel condition) {
        conditions.add(condition);
    }

    @Override
    public boolean matches(KernelIterDescription kid) {

        // Try all conditions
        for (MatchKernel condition : conditions) {

            if (!condition.matches(kid)) {
                return false;
            }
        }
        return true;
    }
}
