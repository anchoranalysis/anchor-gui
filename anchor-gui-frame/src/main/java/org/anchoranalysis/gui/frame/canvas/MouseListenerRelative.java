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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import lombok.AllArgsConstructor;

/**
 * We ignore any of these events if CONTROL or SHIFT is pressed, as we reserve these for ourselves
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class MouseListenerRelative implements MouseListener {

    private BroadcastMouseEvents<MouseListener> broadcast;

    @Override
    public void mouseClicked(MouseEvent event) {
        broadcast.sendChangedEvent(
                event, false, (listener, eventNew) -> listener.mouseClicked(eventNew));
    }

    @Override
    public void mousePressed(MouseEvent event) {
        broadcast.sendChangedEvent(
                event, false, (listener, eventNew) -> listener.mousePressed(eventNew));
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        broadcast.sendChangedEvent(
                event, false, (listener, eventNew) -> listener.mouseReleased(eventNew));
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        broadcast.sendChangedEvent(
                event, true, (listener, eventNew) -> listener.mouseEntered(eventNew));
    }

    @Override
    public void mouseExited(MouseEvent event) {
        broadcast.sendChangedEvent(
                event, true, (listener, eventNew) -> listener.mouseExited(eventNew));
    }
}
