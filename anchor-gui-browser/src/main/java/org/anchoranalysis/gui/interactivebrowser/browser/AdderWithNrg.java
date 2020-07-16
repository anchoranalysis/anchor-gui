/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.browser;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.bean.filecreator.FileCreatorParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.openfile.FileCreatorLoader;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.toolbar.VideoStatsToolbar;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;

class AdderWithNrg {
    private VideoStatsModuleGlobalParams moduleParams;
    private FeatureListSrc featureListSrc;
    private SubgrouppedAdder globalSubgroupAdder;

    public AdderWithNrg(
            VideoStatsModuleGlobalParams moduleParams,
            FeatureListSrc featureListSrc,
            SubgrouppedAdder globalSubgroupAdder) {
        super();
        this.moduleParams = moduleParams;
        this.featureListSrc = featureListSrc;
        this.globalSubgroupAdder = globalSubgroupAdder;
    }

    public void addGlobalSet(VideoStatsToolbar toolbar) throws InitException {
        GlobalDropDown globalDropDown = new GlobalDropDown(globalSubgroupAdder);
        globalDropDown.init(featureListSrc, moduleParams);
        toolbar.add(globalDropDown.getButton());
    }

    public FileCreatorLoader createFileCreatorLoader(
            RasterReader rasterReader,
            FileOpenManager fileOpenManager,
            MarkEvaluatorManager markEvaluatorManager,
            ImporterSettings importerSettings,
            MarkDisplaySettings markDisplaySettings) {

        FileCreatorParams params = new FileCreatorParams();
        params.setMarkCreatorParams(
                new MarkCreatorParams(moduleParams, markDisplaySettings, markEvaluatorManager));
        params.setRasterReader(rasterReader);
        params.setImporterSettings(importerSettings);

        return new FileCreatorLoader(params, fileOpenManager, globalSubgroupAdder);
    }
}
