/* (C)2020 */
package org.anchoranalysis.gui.indices;

import org.anchoranalysis.core.index.IndicesSelection;

// Maintains a record of two selection of indices
//   CURRENT 		- whatever the indices are associated with the current widget state
//                  - this can be changed by both internal and external triggers, and doesn't change
// other widgets state
//
//   LAST-EXPLICIT	- the last time the user changed the indices by interacting with specifically
// this widget
//                  - this can only be changed by internal triggers, and can change other widgets
// state
public class DualIndicesSelection implements IUpdatableIndicesSelection {

    private IndicesSelection currentSelection;
    private IndicesSelection lastExplicitSelection;

    // private static Log log = LogFactory.getLog(DualIndicesSelection.class);

    public DualIndicesSelection() {
        super();
        this.currentSelection = new IndicesSelection();
        this.lastExplicitSelection = new IndicesSelection();
    }

    public DualIndicesSelection(IndicesSelection lastExplicitSelection) {
        super();
        this.currentSelection = new IndicesSelection();
        this.lastExplicitSelection = lastExplicitSelection;
    }

    @Override
    public void updateBoth(int[] ids) {

        // log.debug( String.format("updateBoth %s", new IndicesSelection(ids).toString() ));

        lastExplicitSelection.setCurrentSelection(ids);
        currentSelection.setCurrentSelection(ids);
    }

    @Override
    public boolean setCurrentSelection(int[] ids) {

        if (currentSelection.equalsInt(ids)) {
            return false;
        }

        currentSelection.setCurrentSelection(ids);
        return true;
    }

    public IndicesSelection getCurrentSelection() {
        return currentSelection;
    }

    @Override
    public int[] getSelectedIDs() {
        return currentSelection.getCurrentSelection();
    }

    @Override
    public IndicesSelection getLastExplicitSelection() {
        return lastExplicitSelection;
    }
}
