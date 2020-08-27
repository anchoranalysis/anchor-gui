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

package org.anchoranalysis.gui.finder;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.NamedStacks;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;

public class FinderRasterFilesByManifestDescriptionFunction implements Finder {

    private String function;

    private List<FileWrite> list;

    private RasterReader rasterReader;

    public FinderRasterFilesByManifestDescriptionFunction(
            RasterReader rasterReader, String function) {

        this.function = function;
        this.rasterReader = rasterReader;
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {

        ManifestDescriptionMatchAnd matchManifest = new ManifestDescriptionMatchAnd();
        matchManifest.addCondition(new ManifestDescriptionFunctionMatch(function));
        matchManifest.addCondition(new ManifestDescriptionTypeMatch("raster"));

        list =
                FinderUtilities.findListFile(
                        manifestRecorder, new FileWriteManifestMatch(matchManifest));
        return exists();
    }

    @Override
    public boolean exists() {
        return list != null && !list.isEmpty();
    }

    public NamedStacks createStackCollection() {

        NamedStacks out = new NamedStacks();
        for (FileWrite fileWrite : list) {
            String name = fileWrite.getIndex();

            // Assume single series, single channel
            out.add(
                    name,
                    StoreSupplier.cache(
                            () ->
                                    openStack(
                                            fileWrite.calculatePath(),
                                            rasterReader,
                                            ProgressReporterNull.get())));
        }
        return out;
    }

    private Stack openStack(
            Path filePath, RasterReader rasterReader, ProgressReporter progressReporter)
            throws OperationFailedException {
        try (OpenedRaster openedRaster = rasterReader.openFile(filePath)) {
            return openedRaster.open(0, progressReporter).get(0);

        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
