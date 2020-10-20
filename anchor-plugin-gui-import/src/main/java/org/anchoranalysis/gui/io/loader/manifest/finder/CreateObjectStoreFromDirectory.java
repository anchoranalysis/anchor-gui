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

import java.io.File;
import java.nio.file.Path;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.LazyEvaluationStore;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.io.objects.ObjectCollectionReader;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.apache.commons.io.FilenameUtils;

class CreateObjectStoreFromDirectory {

    public LazyEvaluationStore<ObjectCollection> apply(Path pathFolder, Logger logger)
            throws OperationFailedException {

        LazyEvaluationStore<ObjectCollection> out =
                new LazyEvaluationStore<>("finder object-collections");

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
        out.add(name, ObjectCollectionReader.createFromPathCached(() -> path)::get);
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
