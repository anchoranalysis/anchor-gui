/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.mpp.io.input.MPPInitParamsFactory;

public class OperationInitParams extends CachedOperation<MPPInitParams, CreateException> {

    private OperationWithProgressReporter<NamedProvider<Stack>, ? extends Throwable>
            namedImgStackCollection;
    private Operation<Optional<KeyValueParams>, IOException> keyParams;

    private Define define;

    private BoundIOContext context;

    public OperationInitParams(
            OperationWithProgressReporter<NamedProvider<Stack>, ? extends Throwable>
                    namedImgStackCollection,
            Operation<Optional<KeyValueParams>, IOException> keyParams,
            Define define,
            BoundIOContext context) {
        super();
        this.namedImgStackCollection = namedImgStackCollection;
        this.context = context;
        this.define = define;
        this.keyParams = keyParams;
    }

    // If we've created the proposerShared objects, then we return the names of the available stacks
    // If not, we simply return all possible names
    public Set<String> namesStackCollection() {

        if (isDone()) {
            return this.getResult().getImage().getStackCollection().keys();
        } else {
            Set<String> out = new HashSet<>();
            out.addAll(namesFromListNamedItems(define.getList(StackProvider.class)));

            try {
                out.addAll(namedImgStackCollection.doOperation(ProgressReporterNull.get()).keys());
            } catch (Throwable e) {
                context.getErrorReporter().recordError(OperationInitParams.class, e);
            }
            return out;
        }
    }

    @Override
    protected MPPInitParams execute() throws CreateException {

        // We initialise the markEvaluator
        try {
            return MPPInitParamsFactory.createFromExistingCollections(
                    context,
                    Optional.ofNullable(define),
                    Optional.of(namedImgStackCollection.doOperation(ProgressReporterNull.get())),
                    Optional.empty(),
                    keyParams.doOperation());

        } catch (Throwable e) {
            throw new CreateException(e);
        }
    }

    private static Set<String> namesFromListNamedItems(List<NamedBean<AnchorBean<?>>> list) {

        HashSet<String> out = new HashSet<>();
        for (NamedBean<?> item : list) {
            out.add(item.getName());
        }
        return out;
    }
}
