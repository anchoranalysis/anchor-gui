/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.plugin.io.bean.input.chnl.NamedChnlsBase;

public class ImporterFromNamedChnls extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof NamedChnlsBase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        return Optional.of(
                XMLBeanListHelper.createSingleStack(
                        (InputManager<? extends ProvidesStackInput>) bean, file));
    }
}
