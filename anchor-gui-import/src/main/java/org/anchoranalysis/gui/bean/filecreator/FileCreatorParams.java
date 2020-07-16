/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.params.InputContextParams;

public class FileCreatorParams {

    // Params from InteractiveBrowserInput
    @Getter @Setter private RasterReader rasterReader;

    // Params from General Environment
    @Getter @Setter private MarkCreatorParams markCreatorParams;

    @Getter @Setter private ImporterSettings importerSettings;

    public InputContextParams createInputContext() {
        return markCreatorParams.getModuleParams().createInputContext();
    }

    public Logger getLogErrorReporter() {
        return markCreatorParams.getModuleParams().getLogger();
    }
}
