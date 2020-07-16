/* (C)2020 */
package org.anchoranalysis.gui.finder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.BoundsFromSequenceType;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteAnd;
import org.anchoranalysis.io.manifest.match.FolderWritePath;
import org.anchoranalysis.io.manifest.match.helper.folderwrite.FolderWriteFileFunctionType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class FinderRasterFolder extends FinderSingleFolder {

    private String manifestFunction;

    private BoundedIndexContainer<Stack> result;

    private RasterReader rasterReader;

    private String folderName;

    public FinderRasterFolder(
            String folderName, String manifestFunction, RasterReader rasterReader) {
        super();
        this.manifestFunction = manifestFunction;
        this.rasterReader = rasterReader;
        this.folderName = folderName;
    }

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

    private BoundedIndexContainer<Stack> createCntr(FolderWrite folder) {
        return new BoundsFromSequenceType<>(
                new SequencedFolderRasterReader(folder, rasterReader),
                folder.getAssociatedSequence());
    }

    public BoundedIndexContainer<Stack> get() {
        assert (exists());
        if (result == null) {
            result = createCntr(getFoundFolder());
        }
        return result;
    }

    private static class OperationCreateStack
            extends CachedOperationWithProgressReporter<Stack, OperationFailedException> {

        private SequencedFolderRasterReader sfrr;
        private int index;

        public OperationCreateStack(SequencedFolderRasterReader sfrr, int index) {
            super();
            this.sfrr = sfrr;
            this.index = index;
        }

        @Override
        protected Stack execute(ProgressReporter progressReporter) throws OperationFailedException {
            try {
                return sfrr.get(index);
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedImgStackCollection createStackCollection(boolean namesAsIndexes) {

        if (getFoundFolder() == null) {
            return new NamedImgStackCollection();
        }

        NamedImgStackCollection nisc = null;

        SequencedFolderRasterReader sfrr =
                new SequencedFolderRasterReader(getFoundFolder(), rasterReader);

        SequenceType st = getFoundFolder().getAssociatedSequence();
        int min = st.getMinimumIndex();

        for (int i = min; i != -1; i = st.nextIndex(i)) {
            String name = st.indexStr(i);

            if (nisc == null) {
                nisc = new NamedImgStackCollection();
            }

            if (namesAsIndexes) {
                nisc.addImageStack(String.valueOf(i), new OperationCreateStack(sfrr, i));
            } else {
                nisc.addImageStack(name, new OperationCreateStack(sfrr, i));
            }
        }
        return nisc;
    }
}
