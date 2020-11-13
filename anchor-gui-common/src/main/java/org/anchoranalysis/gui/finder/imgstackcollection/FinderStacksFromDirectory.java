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

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.gui.finder.FinderRasterDirectory;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.io.stack.output.NamedStacksOutputter;
import org.anchoranalysis.io.manifest.Manifest;

// Finds an image stack collection
public class FinderStacksFromDirectory implements FinderStacks {

    private FinderRasterDirectory delegate;

    private NamedStacksSupplier operationStacks =
            NamedStacksSupplier.cache(progress -> delegate.createStackCollection(false));

    public FinderStacksFromDirectory(StackReader stackReader, String directoryName) {
        delegate = new FinderRasterDirectory(directoryName, NamedStacksOutputter.MANIFEST_FUNCTION, stackReader);
    }

    @Override
    public NamedProvider<Stack> getStacks() throws OperationFailedException {
        return operationStacks.get(ProgressIgnore.get());
    }

    @Override
    public NamedStacksSupplier getStacksAsOperation() {
        return operationStacks;
    }

    @Override
    public boolean doFind(Manifest manifestRecorder) {
        return delegate.doFind(manifestRecorder);
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }
}
