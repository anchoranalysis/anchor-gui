/*-
 * #%L
 * anchor-gui-mdi
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

package org.anchoranalysis.gui.mdi.action;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import org.anchoranalysis.gui.mdi.PartitionedFrameList;
import org.anchoranalysis.spatial.point.Point2i;

public class SmartArrangeAction extends AbstractAction {

    private static final long serialVersionUID = -6489545779257029397L;
    private JDesktopPane desktopPane;

    private PartitionedFrameList partitionedFrames;

    public SmartArrangeAction(
            JDesktopPane desktopPane, PartitionedFrameList partitionedFrames, ImageIcon icon) {
        super("", icon);
        putValue(SHORT_DESCRIPTION, "Smart Arrange");

        this.partitionedFrames = partitionedFrames;

        this.desktopPane = desktopPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        arrangeDefaultView(this.desktopPane, this.partitionedFrames);
    }

    public static void arrangeDefaultView(
            JDesktopPane desktopPane, PartitionedFrameList partitionedFrames) {

        Rectangle desktopBounds = desktopPane.getVisibleRect(); // getBounds();

        Point2i max =
                arrangeFixedSizeFrameList(
                        partitionedFrames.getFixedFrames(),
                        desktopBounds,
                        desktopPane.getDesktopManager());

        int desktopHeight = (int) desktopBounds.getHeight();
        int desktopWidth = (int) desktopBounds.getWidth();

        int remainderBottom = desktopHeight - max.y() - 1;

        // We then tile the graph frames in the remaining space either to the left, or to the bottom
        int spaceLeft = (max.x() + 1) * desktopHeight;
        int spaceBottom = remainderBottom * desktopWidth;

        Rectangle bounds = new Rectangle(desktopBounds);
        if (spaceLeft > spaceBottom) {
            bounds.width = max.x() - 1;
        } else {
            bounds.y = max.y() + 1;
            bounds.height = remainderBottom;
        }

        JInternalFrame[] arr =
                partitionedFrames
                        .getDynamicFrames()
                        .toArray(new JInternalFrame[partitionedFrames.getDynamicFrames().size()]);
        tile(arr, bounds, desktopPane.getDesktopManager());
    }

    private static int getMaxWidth(List<JComponent> frames) {

        // We resize the image frames to their preferred size, one above each other on the right
        int maxWidth = 0;
        for (JComponent imageFrame : frames) {

            Dimension preferredSize = imageFrame.getPreferredSize();

            if (preferredSize.getWidth() > maxWidth) {
                maxWidth = (int) preferredSize.getWidth();
            }
        }

        return maxWidth;
    }

    // Returns the starting x coordinate
    private static Point2i arrangeFixedSizeFrameList(
            List<JComponent> frames, Rectangle dBounds, DesktopManager desktopManager) {

        int maxWidth = getMaxWidth(frames);

        int startX = ((int) dBounds.getMaxX()) - maxWidth - 1;
        int runningY = 0;
        int maxY = 0;

        for (int i = 0; i < frames.size(); i++) {

            JComponent imageFrame = frames.get(i);

            Dimension preferredSize = imageFrame.getPreferredSize();

            if ((runningY + preferredSize.height) >= dBounds.getMaxY()) {

                maxY = Math.max(maxY, runningY);

                runningY = 0;
                startX -= (maxWidth + 1);
            }

            desktopManager.resizeFrame(
                    imageFrame, startX, runningY, preferredSize.width, preferredSize.height);

            runningY += preferredSize.height;
        }

        maxY = Math.max(maxY, runningY);

        return new Point2i(startX, maxY);
    }

    private static void tile(
            JInternalFrame[] frames, Rectangle dBounds, DesktopManager desktopManager) {

        int count = frames.length;
        if (count == 0) return;

        // Determine the necessary grid size
        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < count) {
            cols++;
            if (rows * cols < count) {
                rows++;
            }
        }

        // Define some initial values for size & location.
        Dimension size = dBounds.getSize();

        int w = size.width / cols;
        int h = size.height / rows;
        int x = (int) dBounds.getMinX();
        int y = (int) dBounds.getMinY();

        // Iterate over the frames, deiconifying any iconified frames and then
        // relocating & resizing each.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
                JInternalFrame f = frames[(i * cols) + j];

                if (!f.isClosed() && f.isIcon()) {
                    try {
                        f.setIcon(false);
                    } catch (PropertyVetoException ignored) {
                    }
                }

                // We won't go larger than preferred sizes
                Dimension preferredSize = f.getPreferredSize();
                if (w > preferredSize.getWidth()) {
                    w = (int) preferredSize.getWidth();
                }

                if (h > preferredSize.getHeight()) {
                    h = (int) preferredSize.getHeight();
                }

                desktopManager.resizeFrame(f, x, y, w, h);
                x += w;
            }
            y += h; // start the next row
            x = 0;
        }
    }
}
