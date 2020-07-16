/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import java.util.HashMap;
import org.anchoranalysis.core.event.IRoutableEventSourceObject;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public class SubgroupRetriever {

    private HashMap<IRoutableEventSourceObject, VideoStatsModuleSubgroup> mapSubgroup =
            new HashMap<>();

    public void add(IRoutableEventSourceObject module, VideoStatsModuleSubgroup subgroup) {
        mapSubgroup.put(module, subgroup);
    }

    public VideoStatsModuleSubgroup get(IRoutableEventSourceObject module) {
        return mapSubgroup.get(module);
    }

    public void remove(IRoutableEventSourceObject module) {
        mapSubgroup.remove(module);
    }
}
