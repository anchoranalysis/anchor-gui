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

package org.anchoranalysis.gui.frame.details.canvas;

import java.util.Optional;
import javax.swing.JComponent;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.canvas.ImageCanvas;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.link.LinkModules.Adder;

class WrappedSlider implements ISliderState {

    private SliceIndexSlider delegate;

    private InitialSliderState initialState;
    private ChnlSliceRange sliceBounds;
    private IIndexGettableSettable indexCntr;

    private IPropertyValueSendable<Integer> sliceSendable;
    private IPropertyValueSendable<Integer> indexSendable;

    public WrappedSlider(
            ChnlSliceRange sliceBounds,
            BoundedRangeIncompleteDynamic indexBounds,
            InitialSliderState initialState,
            IIndexGettableSettable indexCntr) {
        super();
        this.delegate =
                new SliceIndexSlider(sliceBounds, indexBounds, initialState.isFrameAdjusting());

        delegate.setIndexSliderVisible(initialState.isShowIndexSlider());

        this.initialState = initialState;
        this.sliceBounds = sliceBounds;
        this.indexCntr = indexCntr;

        setupSendable();
    }

    public void configure(ImageCanvas canvas) {

        SliderIndexChanged sliderIndexChanged = new SliderIndexChanged(indexCntr, delegate);

        SliderSliceChanged sliderSliceChanged = new SliderSliceChanged(canvas, delegate);

        configureSlider(sliderSliceChanged, sliderIndexChanged);

        // For the first time we need to trigger the update explicitly, then the event handlers will
        // take over
        sliderSliceChanged.updateFromStackSlider();
        sliderIndexChanged.updateFromIndexSlider();
    }

    private void configureSlider(
            PropertyValueChangeListener<Integer> sliderSliceChanged,
            PropertyValueChangeListener<Integer> sliderIndexChanged) {
        delegate.getSelectIndexReceivable().addPropertyValueChangeListener(sliderIndexChanged);
        delegate.getSelectSliceReceivable().addPropertyValueChangeListener(sliderSliceChanged);

        setSliderSlice(initialState.getSliceNum(), sliceBounds);
        delegate.setIndex(initialState.getIndex(), false);
    }

    @Override
    public int getIndex() {
        return delegate.getIndex();
    }

    @Override
    public int getSliceNum() {
        return delegate.getSliceNum();
    }

    public JComponent getComponent() {
        return delegate.getComponent();
    }

    public SliceIndexSlider getSlider() {
        return delegate;
    }

    public void dispose() {
        delegate.dispose();
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public void setIndexToMaximum() {
        delegate.setIndexToMaximum();
    }

    private void setSliderSlice(int initialSliceNum, ChnlSliceRange sliceBounds) {
        // If it's an invalid negative number, then let's set it to the center, as the user
        //   probably doesn't have any clear slice number preference
        if (initialSliceNum >= sliceBounds.getMinimumIndex()
                && initialSliceNum <= sliceBounds.getMaximumIndex()) {
            delegate.setSliceNum(initialSliceNum);
        } else {
            delegate.setSliceNumToCenter();
        }
    }

    @Override
    public void addSliceTo(Adder<Integer> adder) {
        adder.add(Optional.of(delegate.getSelectSliceReceivable()), Optional.of(sliceSendable));
    }

    @Override
    public void addIndexTo(Adder<Integer> adder) {
        adder.add(Optional.of(delegate.getSelectIndexReceivable()), Optional.of(indexSendable));
    }

    @Override
    public void setSliceNum(int sliceNum) {
        sliceSendable.setPropertyValue(sliceNum, false);
    }

    private void setupSendable() {
        sliceSendable = new SliceSendable(delegate);
        indexSendable = new IndexSendable(indexCntr, delegate, initialState.isFrameAdjusting());
    }
}
