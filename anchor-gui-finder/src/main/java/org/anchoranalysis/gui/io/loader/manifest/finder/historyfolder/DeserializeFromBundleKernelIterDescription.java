/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.io.manifest.deserializer.folder.BundleDeserializers;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderBundle;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializedObjectFromFolderBundle;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

class DeserializeFromBundleKernelIterDescription
        extends DeserializeFromFolderBundle<KernelIterDescription, KernelIterDescription> {

    public DeserializeFromBundleKernelIterDescription(
            BundleDeserializers<KernelIterDescription> deserializer, FolderWrite cfgNRGFolder) {
        super(deserializer, cfgNRGFolder);
    }

    @Override
    protected ITypedGetFromIndex<KernelIterDescription> createCntr(
            DeserializedObjectFromFolderBundle<KernelIterDescription> deserializeFromBundle) {
        return deserializeFromBundle;
    }
}
