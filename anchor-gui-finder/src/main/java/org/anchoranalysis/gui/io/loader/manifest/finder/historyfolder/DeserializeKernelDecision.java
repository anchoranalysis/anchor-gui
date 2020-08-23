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

package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.io.manifest.deserializer.folder.BundleDeserializers;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializeFromFolderBundle;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializedObjectFromFolderBundle;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelDescision;

class DeserializeKernelDecision
        extends DeserializeFromFolderBundle<KernelDescision, KernelDescision> {

    public DeserializeKernelDecision(
            BundleDeserializers<KernelDescision> deserializer, FolderWrite folder) {
        super(deserializer, folder);
    }

    @Override
    protected GetterFromIndex<KernelDescision> createCntr(
            DeserializedObjectFromFolderBundle<KernelDescision> deserializeFromBundle) {
        return deserializeFromBundle;
    }
}