/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;

class SliderIndexChanged implements PropertyValueChangeListener<Integer> {

    private IIndexGettableSettable indexCntr;
    private SliceIndexSlider slider;

    public SliderIndexChanged(IIndexGettableSettable indexCntr, SliceIndexSlider slider) {
        super();
        this.indexCntr = indexCntr;
        this.slider = slider;
    }

    @Override
    public void propertyValueChanged(PropertyValueChangeEvent<Integer> evt) {
        updateFromIndexSlider();
    }

    public void updateFromIndexSlider() {
        indexCntr.setIndex(slider.getIndex());
    }
}
