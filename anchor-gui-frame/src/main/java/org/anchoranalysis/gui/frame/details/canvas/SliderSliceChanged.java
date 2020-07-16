/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.canvas.ImageCanvas;

class SliderSliceChanged implements PropertyValueChangeListener<Integer> {

    private ImageCanvas canvas;
    private SliceIndexSlider slider;

    public SliderSliceChanged(ImageCanvas canvas, SliceIndexSlider slider) {
        super();
        this.canvas = canvas;
        this.slider = slider;
    }

    @Override
    public void propertyValueChanged(PropertyValueChangeEvent<Integer> evt) {
        updateFromStackSlider();
    }

    public void updateFromStackSlider() {
        canvas.changeSlice(slider.getSliceNum());
    }
}
