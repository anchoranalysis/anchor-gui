/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.image.frame;

import java.awt.Dimension;

public abstract class ControllerSize {

    public void configureSize(int width, int height) {
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setMinimumSize(dim);
    }

    /** Sets the minimum and preferred to the same size */
    public void configureSize(
            int minimumWidth, int minimumHeight, int preferredWidth, int preferredHeight) {
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        setMinimumSize(new Dimension(minimumWidth, minimumHeight));
    }

    protected abstract void setMinimumSize(Dimension minimumSize);

    protected abstract void setPreferredSize(Dimension preferredSize);
}
