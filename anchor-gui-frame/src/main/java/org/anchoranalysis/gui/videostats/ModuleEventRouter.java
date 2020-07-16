/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.core.event.IRoutableEventSourceObject;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

// Indicates which modules to route events to
public class ModuleEventRouter {

    private ArrayList<VideoStatsModule> modules = new ArrayList<>();

    private SubgroupRetriever subgroupRetriever;

    public static enum RouterStyle {
        GLOBAL, // All modules
        LOCAL // Only to the associated subgroup
    }

    public ModuleEventRouter(SubgroupRetriever subgroupRetriever) {
        super();
        this.subgroupRetriever = subgroupRetriever;
    }

    public void add(VideoStatsModule o) {
        modules.add(o);
    }

    public boolean remove(VideoStatsModule o) {
        return modules.remove(o);
    }

    public Iterator<VideoStatsModule> destinationModules(
            IRoutableEventSourceObject sourceModule, RouterStyle routerStyle) {

        // For now we build a new list for every module, excluding itself
        // THIS is inefficient, and we should build rather an iterator that skips itself

        ArrayList<VideoStatsModule> destination = new ArrayList<>();

        // assert(subgroupRetriever.get(sourceModule)!=null);

        List<VideoStatsModule> modulesToIter =
                routerStyle == RouterStyle.GLOBAL || sourceModule == null
                        ? modules
                        : subgroupRetriever.get(sourceModule).getListModules();

        for (VideoStatsModule module : modulesToIter) {

            if (module != sourceModule) {
                destination.add(module);
            }
        }

        return destination.iterator();
    }
}
