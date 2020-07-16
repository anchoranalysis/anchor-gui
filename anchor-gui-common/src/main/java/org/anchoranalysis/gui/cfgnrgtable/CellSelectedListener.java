/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import java.util.EventListener;

public interface CellSelectedListener extends EventListener {

    void cellSelected(int row, int column);
}
