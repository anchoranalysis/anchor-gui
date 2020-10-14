/*-
 * #%L
 * anchor-gui-finder
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

package org.anchoranalysis.gui.finder;

import java.util.function.Predicate;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.io.manifest.directory.DirectoryWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleDirectory;
import org.anchoranalysis.io.manifest.finder.match.DirectoryMatch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class FinderHistoryFolder<T> extends FinderSingleDirectory
        implements ContainerGetter<T> {

    private final String manifestFunction;
    
    private BoundedIndexContainer<T> history;

    protected abstract BoundedIndexContainer<T> createFromSerialized(DirectoryWrite folder)
            throws DeserializationFailedException;

    public BoundedIndexContainer<T> get() throws OperationFailedException {

        try {
            assert (exists());

            if (history == null) {
                this.history = createFromSerialized(getFoundDirectory());
            }
        } catch (DeserializationFailedException e) {
            throw new OperationFailedException(e);
        }

        return this.history;
    }

    @Override
    public BoundedIndexContainer<T> getContainer() throws OperationFailedException {
        return get();
    }

    @Override
    protected Predicate<DirectoryWrite> matchDirectories() {
        return DirectoryMatch.description(this.manifestFunction, "serialized");
    }
}
