/* (C)2020 */
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
