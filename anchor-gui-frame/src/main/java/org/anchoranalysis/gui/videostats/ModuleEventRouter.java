/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.videostats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.core.event.RoutableEventSourceObject;
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
            RoutableEventSourceObject sourceModule, RouterStyle routerStyle) {

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
