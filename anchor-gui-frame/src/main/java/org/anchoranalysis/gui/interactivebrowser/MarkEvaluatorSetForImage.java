/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.interactivebrowser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.core.stack.NamedStacksSupplier;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.mpp.io.input.MPPInitParamsFactory;

@RequiredArgsConstructor
public class MarkEvaluatorSetForImage {

    // START REQUIRED ARGUMENTS
    private final NamedStacksSupplier namedStacks;
    private final KeyValueParamsSupplier keyParams;
    private final InputOutputContext context;
    // END REQUIRED ARGUMENTS

    private Map<String, StoreSupplier<MarkEvaluatorResolved>> map = new HashMap<>();

    private class Resolved {

        private CachedSupplier<MPPInitParams, CreateException> operationProposerSharedObjects;
        private MarkEvaluator me;

        public Resolved(MarkEvaluator me) throws CreateException {
            this.me = me;
            operationProposerSharedObjects =
                    /// TODO Do we need this duplication?
                    CachedSupplier.cache(() -> deriveInitParams(me.getDefine().duplicateBean()));

            try {
                // TODO owen, this is causing a bug in the annotorator, we need to get our feature
                // params from somewhere else
                //  i.e. where they are being passed around
                me.initRecursive(
                        operationProposerSharedObjects.get().getFeature(), context.getLogger());
            } catch (InitException e) {
                throw new CreateException(e);
            }
        }

        public MarkEvaluatorResolved get() throws OperationFailedException {
            try {
                return new MarkEvaluatorResolved(
                        operationProposerSharedObjects,
                        me.getMarkFactory(),
                        me.getEnergySchemeCreator().create(),
                        keyParams.get().get()); // NOSONAR
            } catch (CreateException | IOException e) {
                throw new OperationFailedException(e);
            }
        }

        private MPPInitParams deriveInitParams(Define define) throws CreateException {

            // We initialise the markEvaluator
            try {
                return MPPInitParamsFactory.createFromExistingCollections(
                        context,
                        Optional.ofNullable(define),
                        Optional.of(namedStacks.get(ProgressReporterNull.get())),
                        Optional.empty(),
                        keyParams.get());

            } catch (Exception e) {
                throw new CreateException(e);
            }
        }
    }

    public void add(String key, MarkEvaluator me) throws OperationFailedException {
        try {
            map.put(key, StoreSupplier.cache(new Resolved(me)::get));
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public MarkEvaluatorResolved get(String key) throws OperationFailedException {
        StoreSupplier<MarkEvaluatorResolved> op = map.get(key);

        if (op == null) {
            throw new OperationFailedException(
                    String.format("Cannot find markEvaluator '%s'", key));
        }

        return op.get();
    }

    public boolean hasItems() {
        return map.size() > 0;
    }
}
