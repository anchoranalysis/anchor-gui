/*-
 * #%L
 * anchor-plugin-gui-import
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
import java.util.Optional;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterFromBean;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;

public class XMLBean extends OpenFileTypeSingle {

    @Override
    public String[] getExtensions() {
        return NonImageFileFormat.XML.extensionAsArray();
    }

    @Override
    public String getDescription() {
        return "Bean XML File";
    }

    @Override
    protected Optional<FileCreator> creatorForSingleFile(File f, ImporterSettings importerSettings)
            throws CreateException {

        try {
            Object bean = BeanXMLLoader.loadBean(f.toPath());
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
