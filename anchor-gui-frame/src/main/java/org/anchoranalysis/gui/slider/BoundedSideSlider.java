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

package org.anchoranalysis.gui.slider;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.anchoranalysis.core.index.bounded.BoundChangeEvent;
import org.anchoranalysis.core.index.bounded.BoundChangeListener;
import org.anchoranalysis.core.index.bounded.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.core.index.bounded.BoundChangeEvent.BoundType;
import org.anchoranalysis.core.property.IPropertyValueReceivable;

public class BoundedSideSlider {

    private SideSlider slider;

    private BoundedRangeIncompleteDynamic bounds;

    public void dispose() {
        slider.dispose();
        bounds = null;
    }

    private class BoundsChanged implements BoundChangeListener {
        @Override
        public void boundChanged(BoundChangeEvent e) {

            if (e.getBoundType() == BoundType.MAXIMUM) {
                slider.setMaximum(bounds.getMaximumIndex());
            }

            if (e.getBoundType() == BoundType.MINIMUM) {

                slider.setMinimum(bounds.getMinimumIndex());
            }
        }
    }

    private class NextAction extends AbstractAction {

        private static final long serialVersionUID = 248427053814786460L;

        public NextAction() {
            super(">", null);
            // putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            // InputEvent.CTRL_MASK) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int currentIndex = slider.getValue();

            int futureIndex = bounds.nextIndex(currentIndex);
            if (futureIndex != -1) {
                slider.setValue(futureIndex, false);
            } else {
                slider.setValueToMaximum();
            }
        }
    }

    private class PreviousAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = -7968365654484945484L;

        public PreviousAction() {
            super("<", null);
            // putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            // InputEvent.CTRL_MASK) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int currentIndex = slider.getValue();

            int futureIndex = bounds.previousIndex(currentIndex);
            if (futureIndex != -1) {
                slider.setValue(futureIndex, false);
            } else {
                slider.setValueToMinimum();
            }
        }
    }

    public BoundedSideSlider(
            BoundedRangeIncompleteDynamic bounds,
            int tickSpacing,
            int maxNumChars,
            boolean includeAdjusting) {
        super();
        this.slider =
                new SideSlider(
                        bounds.getMinimumIndex(),
                        bounds.getMaximumIndex(),
                        tickSpacing,
                        maxNumChars,
                        includeAdjusting,
                        new NextAction(),
                        new PreviousAction());
        this.bounds = bounds;
        this.bounds.addBoundChangeListener(new BoundsChanged());
    }

    public void setVisible(boolean aFlag) {
        slider.setVisible(aFlag);
    }

    public boolean isVisible() {
        return slider.isVisible();
    }

    public SideSlider getSlider() {
        return slider;
    }

    public int getValue() {
        return slider.getValue();
    }

    public boolean isAtMaximumValue() {
        return slider.isAtMaximumValue();
    }

    public boolean isAtMinimumValue() {
        return slider.isAtMinimumValue();
    }

    public void setValueToCenter() {
        slider.setValueToCenter();
    }

    public void setValueToMaximum() {
        slider.setValueToMaximum();
    }

    public void setValueToMinimum() {
        slider.setValueToMinimum();
    }

    public void setValue(int n, boolean adjusting) {
        slider.setValue(n, adjusting);
    }

    public void setMinimum(int min) {
        slider.setMinimum(min);
    }

    public void setMaximum(int max) {
        slider.setMaximum(max);
    }

    public IPropertyValueReceivable<Integer> getSelectSliceReceivable() {
        return slider.getSelectSliceReceivable();
    }
}
