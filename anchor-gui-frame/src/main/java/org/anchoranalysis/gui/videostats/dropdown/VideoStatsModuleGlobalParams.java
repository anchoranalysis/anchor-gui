/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import java.awt.GraphicsConfiguration;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.io.params.InputContextParams;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;

// Globally available parameters for a VideoStatsModule
@RequiredArgsConstructor
public class VideoStatsModuleGlobalParams {

    // START BEAN PROPERTIES
    @Getter private final ExportPopupParams exportPopupParams;

    @Getter private final CommonContext context;

    @Getter private final InteractiveThreadPool threadPool;

    @Getter private final RandomNumberGenerator randomNumberGenerator;

    @Getter private final ExportTaskList exportTaskList;

    @Getter private final ColorIndex defaultColorIndexForMarks;

    @Getter private final GraphicsConfiguration graphicsCurrentScreen;
    // END BEAN PROPERTIES

    @Getter @Setter private GraphColorScheme graphColorScheme = new GraphColorScheme();

    @Getter
    private RegionMap regionMap = RegionMapSingleton.instance(); // For now we use global regionMaps

    public InputContextParams createInputContext() {
        return new InputContextParams();
    }

    public Logger getLogger() {
        return context.getLogger();
    }

    public Path getModelDirectory() {
        return context.getModelDirectory();
    }
}
