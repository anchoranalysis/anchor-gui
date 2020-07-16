/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWritePath;

@AllArgsConstructor
public class FinderObjectCollectionFolder extends FinderSingleFolder {

    private final String folderName;

    @Override
    protected Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder) {

        List<FolderWrite> list =
                FinderUtilities.findListFolder(manifestRecorder, new FolderWritePath(folderName));

        if (!list.isEmpty()) {
            return Optional.of(list.get(0));
        } else {
            return Optional.empty();
        }
    }

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedProvider<ObjectCollection> createNamedProvider(Logger logger)
            throws OperationFailedException {

        if (getFoundFolder() == null) {
            return new NameValueSet<>();
        }

        return new CreateObjectStoreFromDirectory().apply(getFoundFolder().calcPath(), logger);
    }
}
