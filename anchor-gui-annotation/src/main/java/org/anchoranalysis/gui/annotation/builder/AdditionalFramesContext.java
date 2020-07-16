/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import lombok.Value;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

@Value
public class AdditionalFramesContext {
    private final IAddVideoStatsModule adder;
    private final String name;
    private final VideoStatsModuleGlobalParams mpg;
    private final OutputWriteSettings outputWriteSettings;
}
