/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.property.IPropertyValueSendable;

class IndexSendable implements IPropertyValueSendable<Integer> {

    private IIndexGettableSettable indexCntr;
    private SliceIndexSlider slider;
    private boolean includeIndexAdjusting;

    public IndexSendable(
            IIndexGettableSettable indexCntr,
            SliceIndexSlider slider,
            boolean includeIndexAdjusting) {
        super();
        this.indexCntr = indexCntr;
        this.slider = slider;
        this.includeIndexAdjusting = includeIndexAdjusting;
    }

    @Override
    public void setPropertyValue(Integer index, boolean adjusting) {

        // We ignore any events which are adjusting, unless includeIndexAdjusting mode is on
        if (!includeIndexAdjusting && adjusting) {
            return;
        }

        if (index != indexCntr.getIndex()) {
            indexCntr.setIndex(index);
            slider.setIndex(index, adjusting);
        }
    }
}
