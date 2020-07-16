/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import java.util.EventListener;

@FunctionalInterface
public interface VideoStatsModuleClosedListener extends EventListener {

    public void videoStatsModuleClosed(VideoStatsModuleClosedEvent evt);
}
