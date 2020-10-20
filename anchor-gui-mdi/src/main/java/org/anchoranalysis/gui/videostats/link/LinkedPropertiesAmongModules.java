/*-
 * #%L
 * anchor-gui-mdi
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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.videostats.ModuleEventRouter;
import org.anchoranalysis.gui.videostats.SubgroupRetriever;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.mpp.io.marks.MarksWithDisplayStack;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class LinkedPropertiesAmongModules {

    private LinkedPropertyModuleSet<Integer> linkedFrameIndex;
    private LinkedPropertyModuleSet<Integer> linkedSliceNum;
    private LinkedPropertyModuleSet<IntArray> linkedMarkIndices;
    private LinkedPropertyModuleSet<MarkCollection> linkedOverlays;
    private LinkedPropertyModuleSet<MarksWithDisplayStack> linkedOverlaysWithStack;

    public LinkedPropertiesAmongModules(
            ModuleEventRouter moduleEventRouter, SubgroupRetriever subgroupRetriever) {

        class Factory {
            public <T> LinkedPropertyModuleSet<T> createModuleSet(
                    String uniqueID, T lastPropertyValue) {
                return new LinkedPropertyModuleSet<T>(
                        uniqueID, moduleEventRouter, subgroupRetriever, lastPropertyValue);
            }
        }

        Factory factory = new Factory();

        // Default frame index
        this.linkedFrameIndex = factory.createModuleSet(LinkFramesUniqueID.FRAME_INDEX, 0);
        this.linkedSliceNum = factory.createModuleSet(LinkFramesUniqueID.SLICE_NUM, -1);
        this.linkedMarkIndices =
                factory.createModuleSet(LinkFramesUniqueID.MARK_INDICES, new IntArray());
        this.linkedOverlays = factory.createModuleSet(LinkFramesUniqueID.OVERLAYS, null);
        this.linkedOverlaysWithStack =
                factory.createModuleSet(LinkFramesUniqueID.OVERLAYS_WITH_STACK, null);

        // We send these globally so that the MarkProperties can always pick up on them
        // In the long term we should be able to come up with better routing
        this.linkedOverlays.setSendGlobalEvents(true);
        this.linkedOverlaysWithStack.setSendGlobalEvents(true);
    }

    public List<Action> createActionList() {

        IconFactory rf = new IconFactory();

        ArrayList<Action> actionListIndexToggle = new ArrayList<>();
        actionListIndexToggle.add(
                new LinkModulesAction<>(
                        "Link Frames", linkedFrameIndex, rf.icon("/toolbarIcon/link_frames.png")));
        actionListIndexToggle.add(
                new LinkModulesAction<>(
                        "Link Slices", linkedSliceNum, rf.icon("/toolbarIcon/link_slices.png")));
        actionListIndexToggle.add(
                new LinkModulesAction<>(
                        "Link Marks", linkedMarkIndices, rf.icon("/toolbarIcon/link_marks.png")));
        return actionListIndexToggle;
    }

    public void addModule(VideoStatsModule module) {
        linkedFrameIndex.addModule(module);
        linkedSliceNum.addModule(module);
        linkedMarkIndices.addModule(module);
        linkedOverlays.addModule(module);
        linkedOverlaysWithStack.addModule(module);
    }

    public void removeModule(VideoStatsModule module) {
        linkedFrameIndex.removeModule(module);
        linkedSliceNum.removeModule(module);
        linkedMarkIndices.removeModule(module);
        linkedOverlays.removeModule(module);
        linkedOverlaysWithStack.removeModule(module);
    }

    public int getLastFrameIndex() {
        return linkedFrameIndex.getLastPropertyValueSent();
    }

    public void setFrameIndex(int index) {
        linkedFrameIndex.setPropertyValue(index, false);
    }
}
