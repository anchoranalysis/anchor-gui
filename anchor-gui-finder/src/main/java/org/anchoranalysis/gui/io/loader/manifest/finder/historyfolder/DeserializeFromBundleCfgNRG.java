/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.TypedGetFromIndexBridge;
import org.anchoranalysis.io.manifest.deserializer.folder.BundleDeserializers;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderBundle;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializedObjectFromFolderBundle;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

class DeserializeFromBundleCfgNRG extends DeserializeFromFolderBundle<CfgNRGInstantState, CfgNRG> {

    public DeserializeFromBundleCfgNRG(
            BundleDeserializers<CfgNRG> deserializer, FolderWrite cfgNRGFolder) {
        super(deserializer, cfgNRGFolder);
    }

    @Override
    protected ITypedGetFromIndex<CfgNRGInstantState> createCntr(
            DeserializedObjectFromFolderBundle<CfgNRG> deserializeFromBundle) {
        return new TypedGetFromIndexBridge<>(
                deserializeFromBundle, new CfgNRGInstantStateFromCfgNRGBridge());
    }
}
