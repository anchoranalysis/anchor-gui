/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import java.util.EventObject;

public class VideoStatsModuleClosedEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    /** */
    private VideoStatsModule module;

    public VideoStatsModuleClosedEvent(Object source, VideoStatsModule module) {
        super(source);
        this.module = module;
    }

    public VideoStatsModule getModule() {
        return module;
    }
}
