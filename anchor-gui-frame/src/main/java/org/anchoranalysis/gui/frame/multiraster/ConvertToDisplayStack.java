/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.stack.DisplayStack;

class ConvertToDisplayStack {

    public static DisplayStack apply(NamedRasterSet set) throws OperationFailedException {
        return extractAtIndex(convertBackgroundSet(backgroundFromSet(set)));
    }

    private static BackgroundSet backgroundFromSet(NamedRasterSet set)
            throws OperationFailedException {
        try {
            return set.getBackgroundSet().doOperation(ProgressReporterNull.get());
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException("Cannot create background-set", e.getCause());
        }
    }

    private static BoundedIndexContainer<DisplayStack> convertBackgroundSet(BackgroundSet bg)
            throws OperationFailedException {
        try {
            return bg.getItem(
                            bg.names().iterator().next() // Arbitrary name
                            )
                    .backgroundStackCntr();
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    private static DisplayStack extractAtIndex(BoundedIndexContainer<DisplayStack> indexCntr)
            throws OperationFailedException {
        if (indexCntr.getMinimumIndex() != indexCntr.getMaximumIndex()) {
            throw new OperationFailedException("BackgroundSet has more than one image");
        }
        try {
            return indexCntr.get(indexCntr.getMinimumIndex());
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
