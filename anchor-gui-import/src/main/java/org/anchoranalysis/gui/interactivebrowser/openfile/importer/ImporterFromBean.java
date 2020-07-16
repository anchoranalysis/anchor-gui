/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;

public abstract class ImporterFromBean extends AnchorBean<ImporterFromBean> {

    /** Returns TRUE iff a creator can be made from the bean */
    public abstract boolean isApplicable(Object bean);

    public abstract Optional<FileCreator> create(Object bean, File file) throws CreateException;
}
