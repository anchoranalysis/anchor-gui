/* (C)2020 */
package org.anchoranalysis.gui.frame.singleraster;

import org.anchoranalysis.core.index.IIndexGettableSettable;

class ZeroIndexGetter implements IIndexGettableSettable {

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public void setIndex(int index) {}
}
