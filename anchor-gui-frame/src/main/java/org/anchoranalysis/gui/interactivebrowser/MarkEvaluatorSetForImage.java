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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.mpp.io.input.MarksInitializationFactory;

@RequiredArgsConstructor
public class MarkEvaluatorSetForImage {

    // START REQUIRED ARGUMENTS
    private final NamedStacksSupplier namedStacks;
    private final DictionarySupplier keyParams;
    private final InputOutputContext context;
    // END REQUIRED ARGUMENTS

    private Map<String, StoreSupplier<MarkEvaluatorResolved>> map = new HashMap<>();

    private class Resolved {

        private CachedSupplier<MarksInitialization, CreateException> operationInitialization;
        private MarkEvaluator markEvaluator;

        public Resolved(MarkEvaluator me) throws CreateException {
            this.markEvaluator = me;
            operationInitialization =
                    /// TODO Do we need this duplication?
                    CachedSupplier.cache(
                            () -> deriveInitialization(me.getDefine().duplicateBean()));

            try {
                // TODO owen, this is causing a bug in the annotorator, we need to get our feature
                // params from somewhere else
                //  i.e. where they are being passed around
                me.initRecursive(
                        operationInitialization.get().feature(), context.getLogger());
            } catch (InitException e) {
                throw new CreateException(e);
            }
        }

        public MarkEvaluatorResolved get() throws OperationFailedException {
            try {
                return new MarkEvaluatorResolved(
                        operationInitialization,
                        markEvaluator.getMarkFactory(),
                        markEvaluator.getEnergySchemeCreator().create(),
                        keyParams.get().get()); // NOSONAR
            } catch (CreateException | IOException e) {
                throw new OperationFailedException(e);
            }
        }

        private MarksInitialization deriveInitialization(Define define) throws CreateException {

            SharedObjects sharedObjects = new SharedObjects(context.common());
            ImageInitialization image = new ImageInitialization(sharedObjects);
            
            // We initialise the markEvaluator
            try {
                image.copyStacksFrom(namedStacks.get(ProgressIgnore.get()));
                
                return MarksInitializationFactory.createFromExisting(
                        new InitializationContext(context),
                        Optional.of(define),
                        Optional.of(sharedObjects),
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
