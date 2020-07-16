/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.OpenFileType;

public class ImporterSettings extends AnchorBean<ImporterSettings> {

    // START BEAN FIELDS
    @BeanField @Getter @Setter private List<ImporterFromBean> beanImporters;

    @BeanField @Getter @Setter private List<OpenFileType> openFileImporters;
    // END BEAN FIELDS
}
