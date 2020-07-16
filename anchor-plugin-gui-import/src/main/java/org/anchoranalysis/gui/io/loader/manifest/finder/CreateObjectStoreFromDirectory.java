/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.io.File;
import java.nio.file.Path;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.image.io.objects.ObjectCollectionReader;
import org.anchoranalysis.image.object.ObjectCollection;
import org.apache.commons.io.FilenameUtils;

class CreateObjectStoreFromDirectory {

    public LazyEvaluationStore<ObjectCollection> apply(Path pathFolder, Logger logger)
            throws OperationFailedException {

        LazyEvaluationStore<ObjectCollection> out =
                new LazyEvaluationStore<>(logger, "finder object-collections");

        addHdf5Files(out, pathFolder);

        /** All the sub-directories */
        addSubdirectories(out, pathFolder);

        return out;
    }

    private void addHdf5Files(LazyEvaluationStore<ObjectCollection> out, Path pathFolder)
            throws OperationFailedException {

        for (File file : hd5fFilesFor(pathFolder)) {
            String nameWithoutExt = FilenameUtils.removeExtension(file.getName());
            addPath(out, nameWithoutExt, file.toPath());
        }
    }

    private void addSubdirectories(LazyEvaluationStore<ObjectCollection> out, Path pathFolder)
            throws OperationFailedException {

        for (File dir : subdirectoriesFor(pathFolder)) {
            addPath(out, dir.getName(), dir.toPath());
        }
    }

    private void addPath(LazyEvaluationStore<ObjectCollection> out, String name, Path path)
            throws OperationFailedException {
        out.add(name, ObjectCollectionReader.createFromPathCached(() -> path));
    }

    private static File[] subdirectoriesFor(Path pathFolder) {
        return pathFolder.toFile().listFiles(File::isDirectory);
    }

    private static File[] hd5fFilesFor(Path pathFolder) {
        return pathFolder
                .toFile()
                .listFiles(f -> ObjectCollectionReader.hasHdf5Extension(f.toPath()));
    }
}
