/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

public class MappedFrom<T> {

    private int originalIter;
    private T obj;

    public MappedFrom(int originalIter, T obj) {
        super();
        this.originalIter = originalIter;
        this.obj = obj;
    }

    public int getOriginalIter() {
        return originalIter;
    }

    public T getObj() {
        return obj;
    }
}
