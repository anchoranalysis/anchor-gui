/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.OpenFileType;

public class ImporterSettings extends AnchorBean<ImporterSettings> {

    // START BEAN FIELDS
    @BeanField private List<ImporterFromBean> beanImporters;

    @BeanField private List<OpenFileType> openFileImporters;
    // END BEAN FIELDS

    public List<ImporterFromBean> getBeanImporters() {
        return beanImporters;
    }

    public void setBeanImporters(List<ImporterFromBean> beanImporters) {
        this.beanImporters = beanImporters;
    }

    public List<OpenFileType> getOpenFileImporters() {
        return openFileImporters;
    }

    public void setOpenFileImporters(List<OpenFileType> openFileImporters) {
        this.openFileImporters = openFileImporters;
    }
}
