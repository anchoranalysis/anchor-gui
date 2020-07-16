/* (C)2020 */
package org.anchoranalysis.gui.videostats.link;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.videostats.ModuleEventRouter;
import org.anchoranalysis.gui.videostats.SubgroupRetriever;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.mpp.io.cfg.CfgWithDisplayStack;

public class LinkedPropertiesAmongModules {

    private LinkedPropertyModuleSet<Integer> linkedFrameIndex;
    private LinkedPropertyModuleSet<Integer> linkedSliceNum;
    private LinkedPropertyModuleSet<IntArray> linkedMarkIndices;
    private LinkedPropertyModuleSet<Cfg> linkedOverlays;
    private LinkedPropertyModuleSet<CfgWithDisplayStack> linkedOverlaysWithStack;

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
