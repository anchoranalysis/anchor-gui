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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;

// Manages the various MarkEvaluators that are available in the application
public class MarkEvaluatorManager {

    private Map<String, MarkEvaluator> map = new HashMap<>();

    private InputOutputContext context;

    public MarkEvaluatorManager(InputOutputContext context) {
        super();
        this.context = context;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public MarkEvaluatorSetForImage createSetForStackCollection(
            NamedStacksSupplier namedStacks, DictionarySupplier keyParams) throws CreateException {

        try {
            MarkEvaluatorSetForImage out =
                    new MarkEvaluatorSetForImage(namedStacks, keyParams, context);

            for (Map.Entry<String, MarkEvaluator> entry : map.entrySet()) {
                out.add(entry.getKey(), entry.getValue().duplicateBean());
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
