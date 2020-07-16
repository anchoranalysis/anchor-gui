/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;

public abstract class OpenFileTypeSingle extends OpenFileType {

    @Override
    public List<FileCreator> creatorForFile(List<File> files, ImporterSettings importerSettings)
            throws CreateException {

        List<FileCreator> out = new ArrayList<>();
        for (File f : files) {
            creatorForSingleFile(f, importerSettings).ifPresent(out::add);
        }
        return out;
    }

    protected abstract Optional<FileCreator> creatorForSingleFile(
            File f, ImporterSettings importerSettings) throws CreateException;
}
