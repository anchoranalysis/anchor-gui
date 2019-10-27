package org.anchoranalysis.gui.mdi;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.awt.*;

//http://www.onyxbits.de/content/swing-mdi-coming-window-placement-algorithm-jinternalframes-jdesktoppanes

/**
 * A component implementing this interface may pass placement hints
 * to the Usher.
 */
public interface PlacementHints {

  /**
   * Request the component's origin to be placed within certain bounds.
   * @return null to allow free placement or a bounding rectangle. The
   * rectangle may be larger than the visual area of the container in 
   * which case it'll be clipped.
   */
  public Rectangle desireRegion();
  
  /**
   * Request a placement strategy to be used
   * @return the preferred placement strategy to use
   */
  public int desireStrategy();
}