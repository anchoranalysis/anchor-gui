/* (C)2020 */
package org.anchoranalysis.gui.series;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogUtilities;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.image.stack.TimeSequence;

public class OperationCreateTimeSequence
        extends CachedOperationWithProgressReporter<TimeSequenceProvider, CreateException> {

    private ProvidesStackInput inputObject;
    private int seriesNum;

    public OperationCreateTimeSequence(ProvidesStackInput inputObject, int seriesNum) {
        super();
        this.inputObject = inputObject;
        this.seriesNum = seriesNum;
    }

    @Override
    protected TimeSequenceProvider execute(ProgressReporter progressReporter)
            throws CreateException {
        try {
            return doOperationWithException(progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    private TimeSequenceProvider doOperationWithException(ProgressReporter progressReporter)
            throws OperationFailedException {
        LazyEvaluationStore<TimeSequence> stackCollection =
                new LazyEvaluationStore<>(
                        LogUtilities.createNullErrorReporter(), "createTimeSeries");
        inputObject.addToStore(stackCollection, seriesNum, progressReporter);
        return new TimeSequenceProvider(stackCollection, inputObject.numFrames());
    }
}
