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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JLabel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.spatial.point.Point2i;

@AllArgsConstructor
class UpdateMouseMovedLabel extends MouseMotionAdapter {

    private StringHelper stringConstructor;
    private JLabel detailsLabel;
    private InternalFrameCanvas internalFrameCanvas;

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Point2i point = new Point2i(e.getX(), e.getY());
        if (internalFrameCanvas.canvasContainsAbsolute(point)) {
            updateLabelInside(
                    e.getX(), e.getY(), internalFrameCanvas.intensityStrAtAbsolute(point));
        } else {
            updateLabelOutside(e.getX(), e.getY());
        }
    }

    private void updateLabelOutside(int x, int y) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConstructor.position(x, y));
        sb.append(" ");
        sb.append(stringConstructor.zoom());
        sb.append(" ");
        sb.append(stringConstructor.dataType());
        sb.append(" ");
        sb.append(stringConstructor.resolution());
        sb.append(" ");
        sb.append(stringConstructor.extraString());
        detailsLabel.setText(sb.toString());
    }

    private void updateLabelInside(int x, int y, String intensityStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConstructor.position(x, y));
        sb.append(" ");
        sb.append(intensityStr);
        sb.append(" ");
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
