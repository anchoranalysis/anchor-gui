/*-
 * #%L
 * anchor-gui-import
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

package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.system.path.ExtensionUtilities;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.io.input.bean.files.SpecificPathList;

// Describes what type of file we are opening
public abstract class OpenFileType extends AnchorBean<OpenFileType> {

    public abstract String[] getExtensions();

    public abstract String getDescription();

    public abstract List<FileCreator> creatorForFile(
            List<File> files, ImporterSettings importerSettings) throws CreateException;

    protected static String createName(List<File> files) {
        File firstFile = files.get(0);
        if (files.size() == 1) {
            return ExtensionUtilities.filenameWithoutExtension(firstFile);
        } else {
            return new File(firstFile.getParent()).getName();
        }
    }

    protected static SpecificPathList createFileList(List<File> files) {
        SpecificPathList out = SpecificPathList.createWithEmptyList();

        for (File f : files) {
            out.getListPaths().add(f.getPath());
        }
        return out;
    }

    protected static SpecificPathList createFileList(File f) {
        SpecificPathList out = SpecificPathList.createWithEmptyList();
        out.getListPaths().add(f.getPath());
        return out;
    }
}
