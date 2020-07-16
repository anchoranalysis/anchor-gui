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
