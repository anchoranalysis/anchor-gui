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

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.gui.finder.AddFromSequenceHelper;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderDeserializer;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteAnd;
import org.anchoranalysis.io.manifest.match.FolderWritePath;
import org.anchoranalysis.io.manifest.match.helper.folderwrite.FolderWriteFileFunctionType;

@RequiredArgsConstructor
public class FinderMarksFolder extends FinderSingleFolder {

    // START REQUIRED ARGUMENTS
    private final String manifestFunction;
    private final String folderName;
    // END REQUIRED ARGUMENTS

    private Deserializer<MarkCollection> deserializer = new XStreamDeserializer<>();

    @Override
    protected Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder) {

        FolderWriteAnd folderAdd = new FolderWriteAnd();
        folderAdd.addCondition(new FolderWritePath(folderName));
        folderAdd.addCondition(
                new FolderWriteFileFunctionType(this.manifestFunction, "serialized"));

        List<FolderWrite> list = FinderUtilities.findListFolder(manifestRecorder, folderAdd);

        if (!list.isEmpty()) {
            return Optional.of(list.get(0));
        } else {
            return Optional.empty();
        }
    }

    public NamedProvider<MarkCollection> createNamedProvider(boolean namesAsIndexes)
            throws OperationFailedException {

        if (getFoundFolder() == null) {
            return new NameValueSet<>();
        }

        LazyEvaluationStore<MarkCollection> out = new LazyEvaluationStore<>("finderMarksFolder");

        SequencedFolderDeserializer<MarkCollection> sfrr =
                new SequencedFolderDeserializer<>(getFoundFolder(), deserializer);

        AddFromSequenceHelper.addFromSequence(
                getFoundFolder().getAssociatedSequence(), sfrr, out::add, namesAsIndexes);
        return out;
    }
}
