/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.io.bean.provider.file.SpecificPathList;
import org.apache.commons.io.FilenameUtils;

// Describes what type of file we are opening
public abstract class OpenFileType extends AnchorBean<OpenFileType> {

    public abstract String[] getExtensions();

    public abstract String getDescription();

    public abstract List<FileCreator> creatorForFile(
            List<File> files, ImporterSettings importerSettings) throws CreateException;

    protected static String createName(List<File> files) {
        if (files.size() == 1) {
            File f = files.get(0);
            return FilenameUtils.removeExtension(f.getName());
        } else {
            File f = files.get(0);
            return new File(f.getParent()).getName();
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
