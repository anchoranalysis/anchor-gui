/* (C)2020 */
package org.anchoranalysis.gui.mergebridge;

import java.util.List;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.index.SingleIndexCntr;

// Primary and secondary together, which share a common index
@EqualsAndHashCode(callSuper = true)
public class IndexedDualState<T> extends SingleIndexCntr {

    private List<T> list;

    public IndexedDualState(int index, List<T> list) {
        super(index);
        this.list = list;
    }

    public T getPrimary() {
        return getItemOrNull(0);
    }

    public T getSecondary() {
        return getItemOrNull(1);
    }

    private T getItemOrNull(int index) {
        if (list.size() > index) {
            return list.get(index);
        } else {
            return null;
        }
    }

    public T getItem(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public List<T> getList() {
        return list;
    }
}
