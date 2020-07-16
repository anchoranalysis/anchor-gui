/* (C)2020 */
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
