/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import javax.swing.JOptionPane;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.NrgSchemeCreatorState;

public class ImporterNRGSchemeCreator extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof NRGSchemeCreator;
    }

    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        updateNRGSchemeCreator((NRGSchemeCreator) bean);
        return Optional.empty();
    }

    private static void updateNRGSchemeCreator(NRGSchemeCreator nrgScheme) {
        NrgSchemeCreatorState.instance().setItem(nrgScheme);

        JOptionPane.showMessageDialog(null, "Updated NRGScheme");
    }
}
