/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;

public class ImporterFileCreatorList extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof List;
    }

    @Override
    public Optional<FileCreator> create(Object bean, File file) throws CreateException {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) bean;
        return Optional.of(XMLBeanListHelper.creatorForList(list, file));
    }
}
