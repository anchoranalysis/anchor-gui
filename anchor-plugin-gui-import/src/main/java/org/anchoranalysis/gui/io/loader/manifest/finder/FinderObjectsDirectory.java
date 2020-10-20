/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NameValueSet;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;
import org.anchoranalysis.io.manifest.finder.FinderSingleDirectory;
import org.anchoranalysis.io.manifest.finder.match.DirectoryMatch;

@AllArgsConstructor
public class FinderObjectsDirectory extends FinderSingleDirectory {

    private final String directoryName;

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedProvider<ObjectCollection> createNamedProvider(Logger logger)
            throws OperationFailedException {

        if (!exists()) {
            return new NameValueSet<>();
        }

        return new CreateObjectStoreFromDirectory()
                .apply(getFoundDirectory().calculatePath(), logger);
    }

    @Override
    protected Predicate<MutableDirectory> matchDirectories() {
        return DirectoryMatch.path(directoryName);
    }
}
