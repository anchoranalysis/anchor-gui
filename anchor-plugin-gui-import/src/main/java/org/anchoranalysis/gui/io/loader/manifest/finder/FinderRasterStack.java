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

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.finder.FinderRasterSingleChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReaderUtilities;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleFile;

public abstract class FinderRasterStack extends FinderSingleFile
        implements FinderRasterSingleChnl, BackgroundStackContainer {

    private Optional<Stack> result;

    private RasterReader rasterReader;

    public FinderRasterStack(RasterReader rasterReader, ErrorReporter errorReporter) {
        super(errorReporter);
        this.rasterReader = rasterReader;
    }

    private Stack createStack(FileWrite fileWrite) throws RasterIOException {
        // Assume single series, single channel
        return RasterReaderUtilities.openStackFromPath(rasterReader, fileWrite.calcPath());
    }

    public Stack get() throws OperationFailedException {
        assert (exists());
        if (!result.isPresent()) {
            try {
                result = Optional.of(createStack(getFoundFile()));
            } catch (RasterIOException e) {
                throw new OperationFailedException(e);
            }
        }
        return result.get();
    }

    @Override
    public Channel getFirstChnl() throws OperationFailedException {
        return get().getChannel(0);
    }

    @Override
    public BoundedIndexContainer<DisplayStack> container()
            throws BackgroundStackContainerException {
        try {
            Stack resultNormalized = get().duplicate();

            DisplayStack bgStack = DisplayStack.create(resultNormalized);
            return new SingleContainer<>(bgStack, 0, true);
        } catch (CreateException | OperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }

    @Override
    public int getNumChnl() throws OperationFailedException {
        return get().getNumberChannels();
    }
}
