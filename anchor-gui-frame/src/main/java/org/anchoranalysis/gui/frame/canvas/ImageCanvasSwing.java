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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.bufferedimage.BufferedImageOverlay;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point2i;

@NoArgsConstructor
class ImageCanvasSwing extends JPanel {

    private static final long serialVersionUID = -3283996455215161927L;
    private BufferedImage image;

    private Dimension dimension;

    private Point2i pointOrigin = new Point2i(0, 0);

    private Point2i imageCrnrPoint = pointOrigin;

    public Extent createExtent() {
        int width = Math.max(getWidth(), 1);
        int height = Math.max(getHeight(), 1);
        return new Extent(width, height);
    }

    public void updated(BufferedImage image) {
        this.image = image;
        dimension = new Dimension(image.getWidth(), image.getHeight());
        repaint();
    }

    public boolean hasBeenUpdated() {
        return image != null;
    }

    // Imposes an image at particular X and Y coordinates within the image
    public void updatePart(BufferedImage subImage, int x, int y) {

        assert ((x + subImage.getWidth()) <= dimension.getWidth());
        assert ((y + subImage.getHeight()) <= dimension.getHeight());

        BufferedImageOverlay.overlayBufferedImage(image, subImage, x, y);

        int xGlobal = x + imageCrnrPoint.x();
        int yGlobal = y + imageCrnrPoint.y();
        repaint(xGlobal, yGlobal, subImage.getWidth(), subImage.getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());

        int sx = getWidth();
        int sy = getHeight();

        if (image.getWidth() < sx || image.getWidth() < sy) {

            imageCrnrPoint = new Point2i();
            if (dimension.getWidth() < sx) {
                imageCrnrPoint.setX((sx - image.getWidth()) / 2);
            }
            if (dimension.getHeight() < sy) {
                imageCrnrPoint.setY((sy - image.getHeight()) / 2);
            }
        } else {
            imageCrnrPoint = pointOrigin;
        }

        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(
                image,
                imageCrnrPoint.x(),
                imageCrnrPoint.y(),
                null); // see javadoc for more info on the parameters
    }

    //	@Override
    //	public Dimension getMinimumSize() {
    //		return new Dimension(20,30);
    //	}

    @Override
    public Dimension getMaximumSize() {
        return dimension;
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    public Point2i getImageCrnrPoint() {
        return imageCrnrPoint;
    }
}
