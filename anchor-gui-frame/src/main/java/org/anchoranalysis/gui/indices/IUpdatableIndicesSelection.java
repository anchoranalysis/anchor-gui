/* (C)2020 */
package org.anchoranalysis.gui.indices;

import org.anchoranalysis.core.index.IndicesSelection;

public interface IUpdatableIndicesSelection {

    void updateBoth(int[] ids);

    IndicesSelection getLastExplicitSelection();

    boolean setCurrentSelection(int[] ids);

    int[] getSelectedIDs();
}
