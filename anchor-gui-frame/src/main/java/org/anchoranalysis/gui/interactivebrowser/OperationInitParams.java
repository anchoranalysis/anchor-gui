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
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OperationInitParams extends CachedOperation<MPPInitParams, CreateException> {

    private OperationWithProgressReporter<NamedProvider<Stack>, ? extends Throwable>
            namedStacks;
    private Operation<Optional<KeyValueParams>, IOException> keyParams;
    private Define define;
    private BoundIOContext context;

    // If we've created the proposerShared objects, then we return the names of the available stacks
    // If not, we simply return all possible names
    public Set<String> namedAllAvailableStacks() {

        if (isDone()) {
            return this.getResult().getImage().getStackCollection().keys();
        } else {
            Set<String> out = new HashSet<>();
            out.addAll(namesFromListNamedItems(define.getList(StackProvider.class)));

            try {
                out.addAll(namedStacks.doOperation(ProgressReporterNull.get()).keys());
            } catch (Exception e) {
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
                    Optional.of(namedStacks.doOperation(ProgressReporterNull.get())),
                    Optional.empty(),
                    keyParams.doOperation());

        } catch (Exception e) {
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
