/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTimeAllEach;
import org.anchoranalysis.core.index.IIndexGetter;

public class IterAndExecutionTime implements IIndexGetter {

    private int iter;
    private KernelExecutionTimeAllEach executionTimes;

    // We expect an array where the first item is the total, and subsequent items
    //   are each separate kernel ID
    public IterAndExecutionTime(int iter, KernelExecutionTimeAllEach executionTimes) {
        super();
        this.iter = iter;
        this.executionTimes = executionTimes;
    }

    public int getIter() {
        return iter;
    }

    public void setIter(int iter) {
        this.iter = iter;
    }

    @Override
    public int getIndex() {
        return iter;
    }

    public KernelExecutionTimeAllEach getExecutionTimes() {
        return executionTimes;
    }

    public void setExecutionTimes(KernelExecutionTimeAllEach executionTimes) {
        this.executionTimes = executionTimes;
    }
}
