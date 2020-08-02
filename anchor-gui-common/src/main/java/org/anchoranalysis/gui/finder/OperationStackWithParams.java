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
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.nrg.NRGElemParamsFromImage;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.finder.FinderKeyValueParams;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class OperationStackWithParams
        extends CachedOperationWithProgressReporter<NRGStackWithParams, GetOperationFailedException> {

    private final OperationFindNrgStackFromStacks nrgStackOperation;
    private final FinderKeyValueParams finderImageParams;
    private final FinderSerializedObject<NRGElemParamsFromImage> finderImageParamsLegacy;

    @Override
    protected NRGStackWithParams execute(ProgressReporter progressReporter)
            throws GetOperationFailedException {

        try {
            NRGStackWithParams nrgStackWithParams = nrgStackOperation.doOperation();

            if (finderImageParamsLegacy.exists()) {
                NRGElemParamsFromImage params = finderImageParamsLegacy.get();
                nrgStackWithParams.setParams(params.createKeyValueParams());
            } else if (finderImageParams.exists()) {
                nrgStackWithParams.setParams(finderImageParams.get());
            }

            return nrgStackWithParams;

        } catch (IOException | OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
    }

    public OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getOperationStackCollection() {
        return nrgStackOperation.getOperationStackCollection();
    }
}
