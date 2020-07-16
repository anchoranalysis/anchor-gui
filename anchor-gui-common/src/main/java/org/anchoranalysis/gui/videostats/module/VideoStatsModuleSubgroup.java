/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import java.util.ArrayList;
import java.util.List;

// A subgroup of modules, with which a module communicates events
public class VideoStatsModuleSubgroup {

    private DefaultModuleStateManager defaultStateManager;

    private List<VideoStatsModule> listModules = new ArrayList<>();

    public VideoStatsModuleSubgroup(DefaultModuleState defaultModuleState) {
        defaultStateManager = new DefaultModuleStateManager(defaultModuleState.getLinkState());
    }

    public void addModule(VideoStatsModule module) {
        listModules.add(module);
    }

    public void removeModule(VideoStatsModule module) {
        listModules.remove(module);
    }

    public DefaultModuleStateManager getDefaultModuleState() {
        return defaultStateManager;
    }

    public List<VideoStatsModule> getListModules() {
        return listModules;
    }

    public int size() {
        return listModules.size();
    }
}
