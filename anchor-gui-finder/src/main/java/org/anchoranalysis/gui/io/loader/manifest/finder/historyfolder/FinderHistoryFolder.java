/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
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

    public Operation<LoadContainer<T>, GetOperationFailedException> getAsOperation() {
        return () -> get();
    }

    public LoadContainer<T> get() throws GetOperationFailedException {

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
            throw new GetOperationFailedException(e);
        }

        return this.history;
    }

    @Override
    public BoundedIndexContainer<T> getCntr() throws GetOperationFailedException {
        return get().getCntr();
    }
}
