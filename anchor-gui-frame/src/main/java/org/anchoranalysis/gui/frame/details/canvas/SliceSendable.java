/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import org.anchoranalysis.core.property.IPropertyValueSendable;

class SliceSendable implements IPropertyValueSendable<Integer> {

    private SliceIndexSlider slider;

    public SliceSendable(SliceIndexSlider slider) {
        super();
        this.slider = slider;
    }

    @Override
    public void setPropertyValue(Integer index, boolean adjusting) {
        slider.setSliceNum(index);
    }
}
