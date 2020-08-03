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
import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

// Manages the various MarkEvaluators that are available in the application
public class MarkEvaluatorManager {

    private Map<String, MarkEvaluator> map = new HashMap<>();

    private BoundIOContext context;

    public MarkEvaluatorManager(BoundIOContext context) {
        super();
        this.context = context;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public MarkEvaluatorSetForImage createSetForStackCollection(
            CheckedProgressingSupplier<NamedProvider<Stack>, ? extends Throwable> namedStacks,
            CheckedSupplier<Optional<KeyValueParams>, IOException> keyParams)
            throws CreateException {

        try {
            MarkEvaluatorSetForImage out =
                    new MarkEvaluatorSetForImage(namedStacks, keyParams, context);

            for (String key : map.keySet()) {
                MarkEvaluator me = map.get(key);
                out.add(key, me.duplicateBean());
            }
            return out;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public void add(String key, MarkEvaluator item) {
        map.put(key, item);
    }

    public boolean hasItems() {
        return map.size() > 0;
    }
}
