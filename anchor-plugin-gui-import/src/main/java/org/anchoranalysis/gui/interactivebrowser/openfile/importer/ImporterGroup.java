/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.plugin.io.bean.groupfiles.GroupFiles;

public class ImporterGroup extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof GroupFiles;
    }

    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        return Optional.of(XMLBeanListHelper.createSingleStack((GroupFiles) bean, file));
    }
}
