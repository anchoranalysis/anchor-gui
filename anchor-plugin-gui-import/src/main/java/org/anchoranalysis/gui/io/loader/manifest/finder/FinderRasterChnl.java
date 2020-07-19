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

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.finder.FinderRasterSingleChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleFile;

public abstract class FinderRasterChnl extends FinderSingleFile implements FinderRasterSingleChnl {

    private Optional<Channel> result = Optional.empty();

    private RasterReader rasterReader;

    private boolean normalizeChnl;

    public FinderRasterChnl(
            RasterReader rasterReader, boolean normalizeChnl, ErrorReporter errorReporter) {
        super(errorReporter);
        this.rasterReader = rasterReader;
        this.normalizeChnl = normalizeChnl;
    }

    public Channel get() throws OperationFailedException {
        assert (exists());
        if (!result.isPresent()) {
            try {
                result = Optional.of(createChnl(getFoundFile()));
            } catch (RasterIOException | CreateException e) {
                throw new OperationFailedException(e);
            }
        }
        return result.get();
    }

    @Override
    public Channel getFirstChnl() throws OperationFailedException {
        return get();
    }

    @Override
    public int getNumChnl() {
        return 1;
    }

    private Channel createChnl(FileWrite fileWrite) throws RasterIOException, CreateException {

        // Assume single series, single channel
        Path filePath = fileWrite.calcPath();

        try (OpenedRaster openedRaster = rasterReader.openFile(filePath)) {
            if (openedRaster.numSeries() != 1) {
                throw new CreateException("there must be exactly one series");
            }

            Stack stack = openedRaster.open(0, ProgressReporterNull.get()).get(0);

            if (stack.getNumChnl() != 1) {
                throw new CreateException("there must be exactly one channel");
            }

            if (normalizeChnl) {
                stack.getChnl(0).getVoxelBox().any().multiplyBy(255);
            }

            return stack.getChnl(0);
        }
    }
}
