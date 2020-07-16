/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;

public class ImporterDirect extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof FileCreator;
    }

    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        return Optional.of((FileCreator) bean);
    }
}
