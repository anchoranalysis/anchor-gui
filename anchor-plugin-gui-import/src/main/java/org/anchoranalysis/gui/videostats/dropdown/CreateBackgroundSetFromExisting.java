/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.interactivebrowser.OperationInitParams;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public class CreateBackgroundSetFromExisting
        extends CachedOperationWithProgressReporter<BackgroundSet, GetOperationFailedException> {

    private final OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            existingBackgroundSet;
    private OperationInitParams pso;
    private OutputWriteSettings ows;

    public CreateBackgroundSetFromExisting(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet,
            OperationInitParams pso,
            OutputWriteSettings ows) {
        super();
        this.existingBackgroundSet = backgroundSet;
        this.pso = pso;
        this.ows = ows;
    }

    @Override
    protected BackgroundSet execute(ProgressReporter progressReporter)
            throws GetOperationFailedException {

        BackgroundSet bsExisting = existingBackgroundSet.doOperation(progressReporter);
        try {
            MPPInitParams so = pso.doOperation();
            ImageInitParams soImage = so.getImage();

            return BackgroundSetFactory.createBackgroundSetFromExisting(
                    bsExisting,
                    new WrapStackAsTimeSequence(CreateCombinedStack.apply(soImage)),
                    ows,
                    progressReporter);

        } catch (CreateException e) {
            throw new GetOperationFailedException(e);
        }
    }
}
