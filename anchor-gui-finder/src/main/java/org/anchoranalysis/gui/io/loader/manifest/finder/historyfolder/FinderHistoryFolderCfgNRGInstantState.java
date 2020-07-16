/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.deserializer.folder.BundleDeserializers;
import org.anchoranalysis.io.manifest.deserializer.folder.HistoryCreator;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FinderHistoryFolderCfgNRGInstantState extends FinderHistoryFolder<CfgNRGInstantState> {

    public FinderHistoryFolderCfgNRGInstantState(String manifestFunction) {
        super(manifestFunction);
    }

    @Override
    protected LoadContainer<CfgNRGInstantState> createFromBundle(FolderWrite folder)
            throws DeserializationFailedException {

        BundleDeserializers<CfgNRG> deserializers =
                new BundleDeserializers<>(
                        new ObjectInputStreamDeserializer<Bundle<CfgNRG>>(),
                        new ObjectInputStreamDeserializer<BundleParameters>());
        HistoryCreator<CfgNRGInstantState> cfgNRGHistory =
                new DeserializeFromBundleCfgNRG(deserializers, folder);
        return cfgNRGHistory.create();
    }

    @Override
    protected LoadContainer<CfgNRGInstantState> createFromSerialized(FolderWrite folder)
            throws DeserializationFailedException {

        Deserializer<CfgNRG> deserializer = new ObjectInputStreamDeserializer<>();
        HistoryCreator<CfgNRGInstantState> cfgNRGHistory =
                new DeserializeFromSerializedCfgNRG(deserializer, folder);
        return cfgNRGHistory.create();
    }
}
