/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterFromBean;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;

public class XMLBean extends OpenFileTypeSingle {

    @Override
    public String[] getExtensions() {
        return new String[] {"xml"};
    }

    @Override
    public String getDescription() {
        return "Bean XML File";
    }

    @Override
    protected Optional<FileCreator> creatorForSingleFile(File f, ImporterSettings importerSettings)
            throws CreateException {

        try {
            Object bean = BeanXmlLoader.loadBean(f.toPath());
            return importBean(bean, f, importerSettings.getBeanImporters());

        } catch (BeanXmlException e) {
            throw new CreateException(e);
        }
    }

    private static Optional<FileCreator> importBean(
            Object bean, File file, List<ImporterFromBean> creators) throws CreateException {
        for (ImporterFromBean creator : creators) {
            if (creator.isApplicable(bean)) {
                return creator.create(bean, file);
            }
        }

        throw new CreateException("Bean type unsupported: " + bean.getClass().toGenericString());
    }
}
