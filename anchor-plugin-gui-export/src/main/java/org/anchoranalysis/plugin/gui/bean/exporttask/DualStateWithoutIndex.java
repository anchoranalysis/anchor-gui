/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import java.util.ArrayList;
import java.util.List;

public class DualStateWithoutIndex<T> {

    private List<T> list;

    public DualStateWithoutIndex(T item) {
        list = new ArrayList<>();
        list.add(item);
    }

    public DualStateWithoutIndex(List<T> list) {
        super();
        this.list = list;
    }

    public T getItem(int index) {
        return list.get(index);
    }
}
