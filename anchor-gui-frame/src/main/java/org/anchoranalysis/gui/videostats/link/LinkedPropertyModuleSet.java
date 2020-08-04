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

package org.anchoranalysis.gui.videostats.link;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.anchoranalysis.core.event.RoutableEventSourceObject;
import org.anchoranalysis.core.event.RoutableEvent;
import org.anchoranalysis.core.event.RoutableListener;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.gui.videostats.ModuleEventRouter;
import org.anchoranalysis.gui.videostats.ModuleEventRouter.RouterStyle;
import org.anchoranalysis.gui.videostats.SubgroupRetriever;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule.ReceivableSendablePair;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

// A set of modules are linked on the basis of some common property
//  which can be kept synchronized between the modules
class LinkedPropertyModuleSet<PropertyValueType> {

    private ModuleEventRouter moduleEventRouter;

    private PropertyValueType lastPropertyValue;

    private RoutableListener<PropertyValueChangeEvent<PropertyValueType>> routableListener;

    private SubgroupRetriever subgroupRetriever;

    private String pairKey;

    private boolean enabled = true;

    private boolean sendGlobalEvents =
            false; // If true, we send events to all modules (excluding the source module)
    // If false, we send events only locally (the subgroup, excluding the source module)

    private class Listener
            implements RoutableListener<PropertyValueChangeEvent<PropertyValueType>> {

        @Override
        public void eventOccurred(RoutableEvent<PropertyValueChangeEvent<PropertyValueType>> evt) {

            if (enabled != true) {
                return;
            }

            setPropertyValue(
                    evt.getEvent().getValue(),
                    evt.getEvent().getAdjusting(),
                    evt.getRoutableSource());
        }
    }

    public LinkedPropertyModuleSet(
            String pairKey,
            ModuleEventRouter moduleEventRouter,
            SubgroupRetriever subgroupRetriever,
            PropertyValueType lastPropertyValue) {
        super();
        this.moduleEventRouter = moduleEventRouter;
        this.lastPropertyValue = lastPropertyValue;
        this.pairKey = pairKey;
        this.subgroupRetriever = subgroupRetriever;

        this.routableListener = new Listener();
    }

    public void addModule(VideoStatsModule module) {
        @SuppressWarnings("unchecked")
        ReceivableSendablePair<PropertyValueType> pair =
                module.getReceivableSendablePairMap().get(pairKey);
        if (pair != null && pair.getReceivable() != null) {
            pair.getReceivable().addRoutableListener(routableListener);
        }
    }

    public void removeModule(VideoStatsModule module) {

        @SuppressWarnings("unchecked")
        ReceivableSendablePair<PropertyValueType> pair =
                module.getReceivableSendablePairMap().get(pairKey);

        if (pair != null && pair.getReceivable() != null) {
            pair.getReceivable().removeRoutableListener(routableListener);
        }
    }

    public PropertyValueType getLastPropertyValueSent() {
        return lastPropertyValue;
    }

    public void setPropertyValue(PropertyValueType value, boolean adjusting) {
        setPropertyValue(value, adjusting, null);
    }

    @SuppressWarnings("unchecked")
    public void setPropertyValue(
            PropertyValueType value, boolean adjusting, RoutableEventSourceObject exclude) {

        // For recording the subgroup associated with each module
        Set<VideoStatsModuleSubgroup> subgroupSet = new HashSet<>();

        // Add the subgroup of what we are excluding
        {
            VideoStatsModuleSubgroup subgroup = subgroupRetriever.get(exclude);
            if (subgroup != null) {
                subgroupSet.add(subgroup);
            }
        }

        RouterStyle rs = sendGlobalEvents ? RouterStyle.GLOBAL : RouterStyle.LOCAL;

        Iterator<VideoStatsModule> itr = moduleEventRouter.destinationModules(exclude, rs);
        while (itr.hasNext()) {

            VideoStatsModule module = itr.next();

            ReceivableSendablePair<PropertyValueType> pair =
                    module.getReceivableSendablePairMap().get(pairKey);

            // Get a subgroup from the module
            VideoStatsModuleSubgroup subgroup = subgroupRetriever.get(module);
            if (subgroup != null) {
                subgroupSet.add(subgroup);
            }

            if (pair == null) {
                continue;
            }

            if (pair.getSendable() != null) {
                // System.out.printf("Setting sendable value %s on %s for key '%s'\n", value,
                // module, pairKey );
                pair.getSendable().setPropertyValue(value, adjusting);
            }
        }

        // For every subgroup associated with the module, we also update then with our property
        // value, so long as they
        //  contain the same pair, that we do
        for (VideoStatsModuleSubgroup subgroup : subgroupSet) {
            // Is there an associated sendable property value

            IPropertyValueSendable<PropertyValueType> sendable =
                    subgroup.getDefaultModuleState().getLinkStateManager().getSendable(pairKey);
            if (sendable != null) {
                sendable.setPropertyValue(value, adjusting);
            }
        }

        this.lastPropertyValue = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSendGlobalEvents() {
        return sendGlobalEvents;
    }

    public void setSendGlobalEvents(boolean sendGlobalEvents) {
        this.sendGlobalEvents = sendGlobalEvents;
    }
}
