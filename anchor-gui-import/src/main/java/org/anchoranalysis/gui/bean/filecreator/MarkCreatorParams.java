/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import lombok.Value;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

@Value
public class MarkCreatorParams {
    private final VideoStatsModuleGlobalParams moduleParams;
    private final MarkDisplaySettings markDisplaySettings;
    private final MarkEvaluatorManager markEvaluatorManager;
}
