/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.deserializer.folder.BundleDeserializers;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderSimple;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class FinderHistoryFolderKernelIterDescription
        extends FinderHistoryFolder<KernelIterDescription> {

    public FinderHistoryFolderKernelIterDescription(String manifestFunction) {
        super(manifestFunction);
    }

    @Override
    protected LoadContainer<KernelIterDescription> createFromBundle(FolderWrite folder)
            throws DeserializationFailedException {

        BundleDeserializers<KernelIterDescription> deserializers =
                new BundleDeserializers<>(
                        new ObjectInputStreamDeserializer<Bundle<KernelIterDescription>>(),
                        new ObjectInputStreamDeserializer<BundleParameters>());
        return new DeserializeFromBundleKernelIterDescription(deserializers, folder).create();
    }

    @Override
    protected LoadContainer<KernelIterDescription> createFromSerialized(FolderWrite folder)
            throws DeserializationFailedException {

        Deserializer<KernelIterDescription> deserializer = new ObjectInputStreamDeserializer<>();
        return new DeserializeFromFolderSimple<>(deserializer, folder).create();
    }
}
