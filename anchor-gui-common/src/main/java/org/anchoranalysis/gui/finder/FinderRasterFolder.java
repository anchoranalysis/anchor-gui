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

package org.anchoranalysis.gui.finder;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.NamedStacks;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.BoundsFromSequenceType;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteAnd;
import org.anchoranalysis.io.manifest.match.FolderWritePath;
import org.anchoranalysis.io.manifest.match.helper.folderwrite.FolderWriteFileFunctionType;

@RequiredArgsConstructor
public class FinderRasterFolder extends FinderSingleFolder {

    // START: REQUIRED ARGUMENTS
    private final String folderName;
    private final String manifestFunction;
    private final RasterReader rasterReader;
    // END: REQUIRED ARGUMENTS

    private BoundedIndexContainer<Stack> result;

    @Override
    protected Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder) {

        FolderWriteAnd folderAdd = new FolderWriteAnd();
        folderAdd.addCondition(new FolderWritePath(folderName));
        folderAdd.addCondition(new FolderWriteFileFunctionType(this.manifestFunction, "raster"));

        List<FolderWrite> list = FinderUtilities.findListFolder(manifestRecorder, folderAdd);

        if (!list.isEmpty()) {
            return Optional.of(list.get(0));
        } else {
            return Optional.empty();
        }
    }

    public BoundedIndexContainer<Stack> get() {
        assert (exists());
        if (result == null) {
            result = createContainer(getFoundFolder());
        }
        return result;
    }

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedStacks createStackCollection(boolean namesAsIndexes) {

        if (getFoundFolder() == null) {
            return new NamedStacks();
        }

        NamedStacks nisc = new NamedStacks();

        SequencedFolderRasterReader sfrr =
                new SequencedFolderRasterReader(getFoundFolder(), rasterReader);

        AddFromSequenceHelper.addFromSequenceWithProgressReporter(
                getFoundFolder().getAssociatedSequence(),
                sfrr,
                nisc::addImageStack,
                namesAsIndexes);

        return nisc;
    }

    private BoundedIndexContainer<Stack> createContainer(FolderWrite folder) {
        return new BoundsFromSequenceType<>(
                new SequencedFolderRasterReader(folder, rasterReader),
                folder.getAssociatedSequence());
    }
}
