/* (C)2020 */
package org.anchoranalysis.gui.image;

import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.slider.BoundedSideSlider;
import org.anchoranalysis.gui.slider.SideSlider;

public class IndexSlider {

    private BoundedSideSlider delegate;

    public IndexSlider(BoundedRangeIncompleteDynamic bounds, boolean includeAdjusting) {
        delegate = new BoundedSideSlider(bounds, 1000, 7, includeAdjusting);
        delegate.setVisible(true);
    }

    public void setIndex(Integer index, boolean adjusting) {
        if (index != this.delegate.getValue()) {
            this.delegate.setValue(index, adjusting);
        }
    }

    public int getIndex() {
        return delegate.getValue();
    }

    public void setIndexToMaximum() {
        delegate.setValueToMaximum();
    }

    public boolean isIndexAtMaximum() {
        return delegate.isAtMinimumValue();
    }

    public void setIndexMaximum(int max) {
        delegate.setMaximum(max);
    }

    public void setIndexMinimum(int min) {
        delegate.setMaximum(min);
    }

    public void setVisible(boolean visibility) {
        this.delegate.setVisible(visibility);
    }

    public boolean isVisible() {
        return this.delegate.isVisible();
    }

    public SideSlider getSlider() {
        return delegate.getSlider();
    }

    public IPropertyValueSendable<Integer> getSelectIndexSendable() {
        return new IPropertyValueSendable<Integer>() {

            @Override
            public void setPropertyValue(Integer index, boolean adjusting) {
                setIndex(index, adjusting);
            }
        };
    }

    public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
        return delegate.getSelectSliceReceivable();
    }

    public void dispose() {
        delegate.dispose();
    }
}
