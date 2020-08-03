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

import java.io.IOException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGElemParamsFromImage;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGStackSupplier;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.NamedStacksSupplier;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderKeyValueParams;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;

public class FinderNrgStack implements Finder {

    private FinderRasterFolder finderRasterFolder;
    private NRGStackSupplier nrgStackSupplier;
    private FinderKeyValueParams finderImageParams;
    private FinderSerializedObject<NRGElemParamsFromImage> finderImageParamsLegacy;

    private NamedStacksSupplier operationStacks;

    public FinderNrgStack(RasterReader rasterReader, ErrorReporter errorReporter) {
        finderRasterFolder = new FinderRasterFolder("nrgStack", "nrgStack", rasterReader);

        this.operationStacks = 
                NamedStacksSupplier.cache( progressReporter -> finderRasterFolder.createStackCollection(true) );

        this.finderImageParams = new FinderKeyValueParams("nrgStackParams", errorReporter);
        this.finderImageParamsLegacy =
                new FinderSerializedObject<>("nrgStackImageParams", errorReporter);

        this.nrgStackSupplier = NRGStackSupplier.cache(this::findStack);
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {
        finderImageParams.doFind(manifestRecorder);
        return finderRasterFolder.doFind(manifestRecorder);
    }

    @Override
    public boolean exists() {
        return finderRasterFolder.exists();
    }

    public NRGStackWithParams getNrgStack() throws GetOperationFailedException {
        return nrgStackSupplier.get();
    }

    public NamedProvider<Stack> getNamedStacks() throws OperationFailedException {
        return operationStacks.get(ProgressReporterNull.get());
    }

    public NRGStackSupplier nrgStackSupplier() {
        return nrgStackSupplier;
    }

    public CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
            operationNrgStackWithProgressReporter() {
        return progressReporter -> getNrgStack();
    }
    
    private NRGStackWithParams findStack() throws GetOperationFailedException {

        try {
            NRGStackWithParams nrgStack = FindNrgStackFromStacks.find(operationStacks);

            if (finderImageParamsLegacy.exists()) {
                nrgStack.setParams(finderImageParamsLegacy.get().createKeyValueParams());
            } else if (finderImageParams.exists()) {
                nrgStack.setParams(finderImageParams.get());
            }

            return nrgStack;

        } catch (IOException | OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
    }
}
