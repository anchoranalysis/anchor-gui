/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;

public class OperationNRGStackFromMarkEvaluatorSet
        implements OperationWithProgressReporter<NRGStackWithParams, OperationFailedException>,
                IUpdatableMarkEvaluator {

    private MarkEvaluatorSetForImage markEvaluatorSet;
    private String markEvaluatorIdentifier;

    public OperationNRGStackFromMarkEvaluatorSet(MarkEvaluatorSetForImage markEvaluatorSet) {
        super();
        this.markEvaluatorSet = markEvaluatorSet;
    }

    @Override
    public NRGStackWithParams doOperation(ProgressReporter progressReporter)
            throws OperationFailedException {

        if (markEvaluatorIdentifier == null || markEvaluatorIdentifier.isEmpty()) {
            return null;
        }

        try {
            return markEvaluatorSet.get(markEvaluatorIdentifier).getNRGStack();
        } catch (IllegalArgumentException | GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void setMarkEvaluatorIdentifier(String markEvaluatorIdentifier) {
        this.markEvaluatorIdentifier = markEvaluatorIdentifier;
    }
}
