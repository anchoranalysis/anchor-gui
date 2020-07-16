/*-
 * #%L
 * anchor-gui-browser
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

package org.anchoranalysis.gui.interactivebrowser.openfile;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.OpenFileType;
import org.apache.commons.io.FilenameUtils;

public class OpenFileTypeFactory {

    private List<OpenFileType> list;

    public OpenFileTypeFactory(List<OpenFileType> list) {
        this.list = list;
    }

    public OpenFileType guessTypeFromFile(File f) {
        String extFromFile = FilenameUtils.getExtension(f.getPath());
        for (OpenFileType type : list) {
            String[] exts = type.getExtensions();
            for (String extFromType : exts) {
                if (extFromFile.equalsIgnoreCase(extFromType)) {
                    return type;
                }
            }
        }
        return null;
    }

    // All the types
    public Iterator<OpenFileType> iterator() {
        return list.iterator();
    }

    public OpenFileType factoryFromDescription(String dscr) {
        for (OpenFileType type : list) {
            if (dscr.equalsIgnoreCase(type.getDescription())) {
                return type;
            }
        }
        return null;
    }
}
