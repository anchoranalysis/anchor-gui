/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
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
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class FinderCfgFolder extends FinderSingleFolder {

    private String manifestFunction;

    private Deserializer<Cfg> deserializer = new XStreamDeserializer<>();

    private String folderName;

    public FinderCfgFolder(String folderName, String manifestFunction) {
        super();
        this.manifestFunction = manifestFunction;
        this.folderName = folderName;
    }

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

    private static class OperationCreateCfg extends CachedOperation<Cfg, OperationFailedException> {

        private SequencedFolderDeserializer<Cfg> sfrr;
        private int index;

        public OperationCreateCfg(SequencedFolderDeserializer<Cfg> sfrr, int index) {
            super();
            this.sfrr = sfrr;
            this.index = index;
        }

        @Override
        protected Cfg execute() throws OperationFailedException {
            try {
                return sfrr.get(index);
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedProvider<Cfg> createNamedProvider(boolean namesAsIndexes, Logger logger)
            throws OperationFailedException {

        if (getFoundFolder() == null) {
            return new NameValueSet<>();
        }

        LazyEvaluationStore<Cfg> out = new LazyEvaluationStore<>(logger, "finderCfgFolder");

        SequencedFolderDeserializer<Cfg> sfrr =
                new SequencedFolderDeserializer<>(getFoundFolder(), deserializer);

        SequenceType st = getFoundFolder().getAssociatedSequence();
        int min = st.getMinimumIndex();

        for (int i = min; i != -1; i = st.nextIndex(i)) {
            String name = st.indexStr(i);

            if (namesAsIndexes) {
                out.add(String.valueOf(i), new OperationCreateCfg(sfrr, i));
            } else {
                out.add(name, new OperationCreateCfg(sfrr, i));
            }
        }
        return out;
    }
}
