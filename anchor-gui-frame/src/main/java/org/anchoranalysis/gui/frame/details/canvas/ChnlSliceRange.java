/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.index.container.BoundedRange;
import org.anchoranalysis.image.extent.ImageDimensions;

@AllArgsConstructor
class ChnlSliceRange implements BoundedRange {

    private final ImageDimensions dimensions;

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return (dimensions.getZ() - 1);
    }
}
