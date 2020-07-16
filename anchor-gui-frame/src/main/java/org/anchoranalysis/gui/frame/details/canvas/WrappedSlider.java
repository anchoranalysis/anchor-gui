/* (C)2020 */
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
