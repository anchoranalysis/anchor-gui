/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

public class OperationCreateBackgroundSet
        extends CachedOperationWithProgressReporter<BackgroundSet, GetOperationFailedException> {

    private OperationWithProgressReporter<TimeSequenceProvider, ? extends Throwable>
            namedImgStackCollection;

    public OperationCreateBackgroundSet(NamedProvider<Stack> namedProvider) {
        this(
                progressReporter ->
                        new TimeSequenceProvider(new WrapStackAsTimeSequence(namedProvider), 1));
    }

    public OperationCreateBackgroundSet(
            OperationWithProgressReporter<TimeSequenceProvider, ? extends Throwable>
                    namedImgStackCollection) {
        super();
        this.namedImgStackCollection = namedImgStackCollection;
    }

    @Override
    protected BackgroundSet execute(ProgressReporter progressReporter)
            throws GetOperationFailedException {
        try {
            NamedProvider<TimeSequence> stacks =
                    namedImgStackCollection.doOperation(progressReporter).sequence();

            BackgroundSet backgroundSet =
                    BackgroundSetFactory.createBackgroundSet(stacks, ProgressReporterNull.get());

            return backgroundSet;
        } catch (Throwable e) {
            throw new GetOperationFailedException(e);
        }
    }
}
