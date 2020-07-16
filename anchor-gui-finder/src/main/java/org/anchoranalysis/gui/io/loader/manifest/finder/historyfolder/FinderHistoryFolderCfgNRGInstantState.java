/*-
 * #%L
 * anchor-gui-finder
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
