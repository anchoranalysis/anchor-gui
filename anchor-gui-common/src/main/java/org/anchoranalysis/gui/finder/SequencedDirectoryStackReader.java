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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.OpenedRaster;
import org.anchoranalysis.io.manifest.directory.sequenced.DeriveElementsFromSequencedDirectory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectory;

/**
 * Reads a stack from each file in a directory with a sequence of files.
 *
 * <p>Only the first time-point of the first series is opened.
 *
 * @author Owen Feehan
 */
class SequencedDirectoryStackReader extends DeriveElementsFromSequencedDirectory<Stack> {

    private StackReader stackReader;

    public SequencedDirectoryStackReader(SequencedDirectory rootDirectory, StackReader stackReader) {
        super(rootDirectory);
        this.stackReader = stackReader;
    }

    @Override
    protected Stack createFromFile(Path path) throws CreateException {
        // We don't support multiple series for now
        try {
            OpenedRaster openedRaster = stackReader.openFile(path);
            try {
                return openedRaster.open(0, ProgressIgnore.get()).get(0);
            } finally {
                openedRaster.close();
            }
        } catch (ImageIOException e) {
            throw new CreateException(e);
        }
    }
}
