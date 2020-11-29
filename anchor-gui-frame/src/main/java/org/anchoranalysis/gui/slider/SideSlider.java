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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.gui.property.PropertyValueChangeEvent;
import org.anchoranalysis.gui.property.PropertyValueChangeListener;

public class SideSlider extends JPanel {

    // private static Log log = LogFactory.getLog(SideSlider.class);

    private class IncrementAction extends AbstractAction {

        private static final long serialVersionUID = 1109874476364298133L;
        /** */
        public IncrementAction() {
            super("+", null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            increment();
        }
    }

    private class DecrementAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = 2929243083249409751L;
        /** */
        public DecrementAction() {
            super("-", null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            decrement();
        }
    }

    private class SliderStateChange implements ChangeListener {

        private boolean incomingAdjusting = false;

        @Override
        public void stateChanged(ChangeEvent e) {

            if (ignoreChangeEvents) {
                return;
            }

            boolean isAdjusting = slider.getValueIsAdjusting() || incomingAdjusting;
            if (includeAdjusting || !isAdjusting) {

                updateLabel(slider.getValue());
                onIndexChanged(isAdjusting);
            }
        }

        public void startIncomingAdjusting() {
            this.incomingAdjusting = true;
        }

        public void endIncomingAdjusting() {
            this.incomingAdjusting = false;
        }
    }

    private class TextChange implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {

            int targetValue = Integer.parseInt(textField.getText());
            setValue(targetValue, false);
        }
    }

    /** */
    private static final long serialVersionUID = -2702699098151451787L;

    private JSlider slider;

    private JTextField textField = new JTextField();

    private EventListenerList listeners = new EventListenerList();

    private boolean ignoreChangeEvents = false;

    private int tickSpacing;

    private boolean includeAdjusting = true;

    private SliderStateChange sliderStateChanged;

    private JPanel buttonPanel;

    private void constructorInit(
            int min,
            int max,
            int tickSpacing,
            int maxNumChars,
            boolean includeAdjusting,
            AbstractAction nextAction,
            AbstractAction prevAction) {

        this.tickSpacing = tickSpacing;
        this.includeAdjusting = includeAdjusting;

        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        this.slider = new JSlider(SwingConstants.HORIZONTAL, min, max, (min + max) / 2);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        add(this.slider, BorderLayout.CENTER);

        sliderStateChanged = new SliderStateChange();

        slider.setMajorTickSpacing(tickSpacing);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(true);

        // textField.setText("  ");

        updateLabel(slider.getValue());

        Font monospaced = Font.getFont("monospaced");

        this.textField.setHorizontalAlignment(SwingConstants.RIGHT);
        this.textField.setFont(monospaced);
        this.textField.setColumns(maxNumChars);
        this.textField.addActionListener(new TextChange());
        add(this.textField, BorderLayout.WEST);

        // We create two actions, to increase the slider, and decrease it, and assign
        //  them to buttons on the right side of our pane

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.add(new JButton(nextAction), BorderLayout.EAST);
        buttonPanel.add(new JButton(prevAction), BorderLayout.WEST);
        add(buttonPanel, BorderLayout.EAST);

        slider.addChangeListener(sliderStateChanged);
    }

    public SideSlider(
            int min, int max, int tickSpacing, int maxNumChars, boolean includeAdjusting) {
        super();
        constructorInit(
                min,
                max,
                tickSpacing,
                maxNumChars,
                includeAdjusting,
                new IncrementAction(),
                new DecrementAction());
    }

    public SideSlider(
            int min,
            int max,
            int tickSpacing,
            int maxNumChars,
            boolean includeAdjusting,
            AbstractAction nextAction,
            AbstractAction prevAction) {
        super();
        constructorInit(
                min, max, tickSpacing, maxNumChars, includeAdjusting, nextAction, prevAction);
    }

    public IPropertyValueReceivable<Integer> getSelectSliceReceivable() {
        return new IPropertyValueReceivable<Integer>() {

            @Override
            public void addPropertyValueChangeListener(PropertyValueChangeListener<Integer> l) {
                listeners.add(PropertyValueChangeListener.class, l);
            }

            @Override
            public void removePropertyValueChangeListener(PropertyValueChangeListener<Integer> l) {
                listeners.remove(PropertyValueChangeListener.class, l);
            }
        };
    }

    @Override
    public void setVisible(boolean aFlag) {
        slider.setVisible(aFlag);
        textField.setVisible(aFlag);
        buttonPanel.setVisible(aFlag);
    }

    @Override
    public boolean isVisible() {
        return slider.isVisible();
    }

    public void setValueToCenter() {

        int v = (getMinimum() + getMaximum()) / 2;
        setValue(v, false);
    }

    public void setValueToMaximum() {
        setValue(getMaximum(), false);
    }

    public void setValueToMinimum() {
        setValue(getMinimum(), false);
    }

    public boolean isAtMaximumValue() {
        return (slider.getValue() == slider.getMaximum());
    }

    public boolean isAtMinimumValue() {
        return (slider.getValue() == slider.getMinimum());
    }

    @SuppressWarnings("unchecked")
    private void onIndexChanged(boolean adjusting) {

        for (PropertyValueChangeListener<Integer> cl :
                listeners.getListeners(PropertyValueChangeListener.class)) {
            cl.propertyValueChanged(
                    new PropertyValueChangeEvent<>(this, slider.getValue(), adjusting));
        }
    }

    public void increment() {
        int val = slider.getValue();

        val += tickSpacing;

        if (val <= slider.getMaximum()) {
            slider.setValue(val);
        } else {
            slider.setValue(slider.getMaximum());
        }
    }

    public void decrement() {
        int val = slider.getValue();

        val -= tickSpacing;

        if (val >= slider.getMinimum()) {
            slider.setValue(val);
        } else {
            slider.setValue(slider.getMinimum());
        }
    }

    private void updateLabel(int val) {
        String updateStr = val + "";

        // Only update if different
        if (!updateStr.equals(textField.getText())) {
            textField.setText(updateStr);
        }
    }

    private int getTextValue() {
        return Integer.valueOf(textField.getText().trim());
    }

    public int getMinimum() {
        return slider.getMinimum();
    }

    public void setMinimum(int min) {

        // what happens if the maximum is decreased?
        ignoreChangeEvents = true;
        slider.setMinimum(min);
        if (getTextValue() < min) {
            updateLabel(min);
        }
        ignoreChangeEvents = false;
    }

    public int getMaximum() {
        return slider.getMaximum();
    }

    public void setMaximum(int max) {

        // what happens if the maximum is decreased?

        ignoreChangeEvents = true;
        slider.setMaximum(max);
        if (getTextValue() > max) {
            updateLabel(max);
        }
        ignoreChangeEvents = false;
    }

    public void setValue(int n, boolean adjusting) {

        // We set the incomingAdjusting flag, so that
        //  the event listener knows that any change
        //  in value was triggered by an adjustment
        if (adjusting) {
            sliderStateChanged.startIncomingAdjusting();
        }
        slider.setValue(n);
        if (adjusting) {
            sliderStateChanged.endIncomingAdjusting();
        }
    }

    public int getValue() {
        return slider.getValue();
    }

    public boolean getValueIsAdjusting() {
        return slider.getValueIsAdjusting();
    }

    public void dispose() {
        slider = null;
        listeners = null;
    }
}
