/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.finder.imgstackcollection;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.finder.FindFailedException;

/** Combines a number of other {@link FinderStacks} */
public class FinderStacksCombine implements FinderStacks {

    private List<FinderStacks> list = new ArrayList<>();

    private NamedStacksSupplier operation =
            NamedStacksSupplier.cache(
                    pr -> {
                        NamedStacks out = new NamedStacks();

                        for (FinderStacks finder : list) {
                            out.addFrom(finder.getStacks());
                        }

                        return out;
                    });

    @Override
    public NamedProvider<Stack> getStacks() throws OperationFailedException {
        return operation.get(ProgressReporterNull.get());
    }

    @Override
    public NamedStacksSupplier getStacksAsOperation() {
        return operation;
    }

    @Override
    public boolean doFind(Manifest manifestRecorder) throws FindFailedException {
        boolean result = false;
        for (FinderStacks finder : list) {
            if (finder.doFind(manifestRecorder)) {
                result = true;
            }
        }
        return result;
    }

    // Uses OR behaviour, so returns true if any of the elements exist
    @Override
    public boolean exists() {
        for (FinderStacks finder : list) {
            if (finder.exists()) {
                return true;
            }
        }
        return false;
    }

    public void add(FinderStacks finder) {
        list.add(finder);
    }
}
