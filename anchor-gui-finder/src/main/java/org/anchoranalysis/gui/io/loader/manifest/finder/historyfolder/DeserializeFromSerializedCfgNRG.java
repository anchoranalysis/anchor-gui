/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.TypedGetFromIndexBridge;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolder;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderDeserializer;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

class DeserializeFromSerializedCfgNRG extends DeserializeFromFolder<CfgNRGInstantState> {

    private Deserializer<CfgNRG> deserializer;

    public DeserializeFromSerializedCfgNRG(
            Deserializer<CfgNRG> deserializer, SequencedFolder folder) {
        super(folder);
        this.deserializer = deserializer;
    }

    @Override
    protected ITypedGetFromIndex<CfgNRGInstantState> createCtnr(SequencedFolder folder) {
        return new TypedGetFromIndexBridge<>(
                new SequencedFolderDeserializer<>(folder, deserializer),
                new CfgNRGInstantStateFromCfgNRGBridge());
    }
}
