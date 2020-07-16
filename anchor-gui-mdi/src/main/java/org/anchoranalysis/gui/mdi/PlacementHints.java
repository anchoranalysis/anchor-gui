/* (C)2020 */
package org.anchoranalysis.gui.mdi;

import java.awt.*;

// http://www.onyxbits.de/content/swing-mdi-coming-window-placement-algorithm-jinternalframes-jdesktoppanes

public interface PlacementHints {

    /**
     * Request the component's origin to be placed within certain bounds.
     *
     * @return null to allow free placement or a bounding rectangle. The rectangle may be larger
     *     than the visual area of the container in which case it'll be clipped.
     */
    public Rectangle desireRegion();

    /**
     * Request a placement strategy to be used
     *
     * @return the preferred placement strategy to use
     */
    public int desireStrategy();
}
