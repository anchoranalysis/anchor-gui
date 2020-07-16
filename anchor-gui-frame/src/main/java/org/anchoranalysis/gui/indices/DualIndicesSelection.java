/*-
 * #%L
 * anchor-gui-frame
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
