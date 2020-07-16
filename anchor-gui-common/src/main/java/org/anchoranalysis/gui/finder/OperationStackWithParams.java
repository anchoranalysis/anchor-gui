/* (C)2020 */
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

class OperationStackWithParams
        extends CachedOperationWithProgressReporter<NRGStackWithParams, OperationFailedException> {

    private OperationFindNrgStackFromStackCollection nrgStackOperation;
    private FinderKeyValueParams finderImageParams;
    private FinderSerializedObject<NRGElemParamsFromImage> finderImageParamsLegacy;

    public OperationStackWithParams(
            OperationFindNrgStackFromStackCollection nrgStackOperation,
            FinderKeyValueParams finderImageParams,
            FinderSerializedObject<NRGElemParamsFromImage> finderImageParamsLegacy) {
        super();
        this.nrgStackOperation = nrgStackOperation;
        this.finderImageParams = finderImageParams;
        this.finderImageParamsLegacy = finderImageParamsLegacy;
    }

    @Override
    protected NRGStackWithParams execute(ProgressReporter progressReporter)
            throws OperationFailedException {

        try {
            NRGStackWithParams nrgStackWithParams = nrgStackOperation.doOperation();

            if (finderImageParamsLegacy.exists()) {
                NRGElemParamsFromImage params = finderImageParamsLegacy.get();
                nrgStackWithParams.setParams(params.createKeyValueParams());
            } else if (finderImageParams.exists()) {
                nrgStackWithParams.setParams(finderImageParams.get());
            }

            return nrgStackWithParams;

        } catch (GetOperationFailedException | IOException e) {
            throw new OperationFailedException(e);
        }
    }

    public OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getOperationStackCollection() {
        return nrgStackOperation.getOperationStackCollection();
    }
}
