/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.finder;

import java.nio.file.Path;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.io.stack.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderContainerCreator;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

class SequencedFolderStackReader extends SequencedFolderContainerCreator<Stack> {

    private StackReader stackReader;

    public SequencedFolderStackReader(SequencedFolder rootFolder, StackReader stackReader) {
        super(rootFolder);
        this.stackReader = stackReader;
    }

    @Override
    protected Stack createFromFilePath(Path path) throws CreateException {
        // We don't support multiple series for now
        try {
            OpenedRaster or = stackReader.openFile(path);
            try {
                return or.open(0, ProgressReporterNull.get()).get(0);
            } finally {
                or.close();
            }
        } catch (ImageIOException e) {
            throw new CreateException(e);
        }
    }
}
