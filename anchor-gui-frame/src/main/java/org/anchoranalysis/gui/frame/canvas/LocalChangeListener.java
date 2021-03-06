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

package org.anchoranalysis.gui.frame.canvas;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class LocalChangeListener implements ChangeListener {

    // START REQUIRED ARGUMENTS
    private final boolean includeAdjusting;
    // END REQUIRED ARGUMENTS

    private List<ChangeListener> listeners = new ArrayList<>();
    private boolean eventsAllowed = true;

    public synchronized void enableEvents() {
        eventsAllowed = true;
    }

    public synchronized void disableEvents() {
        eventsAllowed = false;
    }

    @Override
    public synchronized void stateChanged(ChangeEvent changeEvent) {

        Object source = changeEvent.getSource();
        if (source instanceof BoundedRangeModel) {
            BoundedRangeModel model = (BoundedRangeModel) source;
            if (eventsAllowed) {

                if (includeAdjusting || !model.getValueIsAdjusting()) {
                    for (ChangeListener cl : listeners) {
                        cl.stateChanged(changeEvent);
                    }
                }
            }
        }
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
}
