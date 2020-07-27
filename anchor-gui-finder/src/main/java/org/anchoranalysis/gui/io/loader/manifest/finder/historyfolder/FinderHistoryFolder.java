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

package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.helper.folderwrite.FolderWriteFileFunctionType;

public abstract class FinderHistoryFolder<T> extends FinderSingleFolder
        implements ContainerGetter<T> {

    private LoadContainer<T> history;

    private String manifestFunction;

    private enum StorageType {
        BUNDLE,
        NON_BUNDLE
    }

    // We remember what type from when we found the file, so
    //   that we can use it again when deserializing
    private StorageType foundStorage;

    public FinderHistoryFolder(String manifestFunction) {
        super();
        this.manifestFunction = manifestFunction;
    }

    // A simple method to override in each finder that is based upon finding a single file
    @Override
    protected Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder) {

        List<FolderWrite> incrementalListBundle =
                FinderUtilities.findListFolder(
                        manifestRecorder,
                        new FolderWriteFileFunctionType(this.manifestFunction, "serializedBundle"));
        if (!incrementalListBundle.isEmpty()) {
            foundStorage = StorageType.BUNDLE;
            return Optional.of(incrementalListBundle.get(0));
        }

        List<FolderWrite> incrementalList =
                FinderUtilities.findListFolder(
                        manifestRecorder,
                        new FolderWriteFileFunctionType(this.manifestFunction, "serialized"));

        // We take the frame from the first one
        if (!incrementalList.isEmpty()) {
            foundStorage = StorageType.NON_BUNDLE;
            return Optional.of(incrementalList.get(0));
        }

        return Optional.empty();
    }

    protected abstract LoadContainer<T> createFromBundle(FolderWrite folder)
            throws DeserializationFailedException;

    protected abstract LoadContainer<T> createFromSerialized(FolderWrite folder)
            throws DeserializationFailedException;

    public Operation<LoadContainer<T>, OperationFailedException> getAsOperation() {
        return () -> get();
    }

    public LoadContainer<T> get() throws OperationFailedException {

        try {
            assert (exists());

            if (history == null) {

                switch (foundStorage) {
                    case BUNDLE:
                        this.history = createFromBundle(getFoundFolder());
                        break;
                    case NON_BUNDLE:
                        this.history = createFromSerialized(getFoundFolder());
                        break;
                    default:
                        assert false;
                        break;
                }
            }
        } catch (DeserializationFailedException e) {
            throw new OperationFailedException(e);
        }

        return this.history;
    }

    @Override
    public BoundedIndexContainer<T> getCntr() throws OperationFailedException {
        return get().getCntr();
    }
}
