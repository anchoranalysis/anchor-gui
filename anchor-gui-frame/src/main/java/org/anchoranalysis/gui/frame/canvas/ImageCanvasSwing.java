/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.bufferedimage.BufferedImageOverlay;

class ImageCanvasSwing extends JPanel {

    private static final long serialVersionUID = -3283996455215161927L;
    private BufferedImage image;

    private Dimension dimension;

    private Point2i pointOrigin = new Point2i(0, 0);

    private Point2i imageCrnrPoint = pointOrigin;

    public ImageCanvasSwing() {}

    public Extent createExtent() {
        return new Extent(getWidth(), getHeight(), 1);
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

        int xGlobal = x + imageCrnrPoint.getX();
        int yGlobal = y + imageCrnrPoint.getY();
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
                imageCrnrPoint.getX(),
                imageCrnrPoint.getY(),
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
