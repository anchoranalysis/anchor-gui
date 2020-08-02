/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.io.loader.manifest.finder;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.DisplayStack;

// We can eliminate this finder in a moment
public abstract class FinderRasterChnlZeroOne extends FinderRasterChnl
        implements BackgroundStackContainer {

    public FinderRasterChnlZeroOne(RasterReader rasterReader, ErrorReporter errorReporter) {
        super(rasterReader, true, errorReporter);
    }

    @Override
    public BoundedIndexContainer<DisplayStack> container()
            throws BackgroundStackContainerException {
        return new SingleContainer<>(backgroundStack(), 0, true);
    }

    public DisplayStack backgroundStack() throws BackgroundStackContainerException {
        try {
            Channel background = getFirstChnl();
            return DisplayStack.create(background);
        } catch (CreateException | OperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }
}
