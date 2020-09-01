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

package org.anchoranalysis.gui.frame.details;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class StackChangedOrMouseExited extends MouseAdapter implements ChangeListener {

    private StringHelper stringConstructor;
    private JLabel detailsLabel;

    public StackChangedOrMouseExited(StringHelper stringConstructor, JLabel detailsLabel) {
        super();
        this.stringConstructor = stringConstructor;
        this.detailsLabel = detailsLabel;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        updateLabelNoPosition();
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
        updateLabelNoPosition();
    }

    private void updateLabelNoPosition() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConstructor.zoom());
        sb.append(" ");
        sb.append(stringConstructor.dataType());
        sb.append(" ");
        sb.append(stringConstructor.resolution());
        sb.append(" ");
        sb.append(stringConstructor.extraString());
        detailsLabel.setText(sb.toString());
    }
}
