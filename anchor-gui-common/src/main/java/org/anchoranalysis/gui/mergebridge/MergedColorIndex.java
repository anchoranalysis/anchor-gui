/* (C)2020 */
package org.anchoranalysis.gui.mergebridge;

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

public class MergedColorIndex implements ColorIndex {

    private ColorList colorList = new ColorList();
    private MergeCfgBridge mergeCfgBridge;

    private static RGBColor colorForProposalState(ProposalState proposalState) {
        switch (proposalState) {
            case UNCHANGED:
                return new RGBColor(0, 0, 255);
            case MODIFIED_ORIGINAL:
                return new RGBColor(132, 112, 255);
            case MODIFIED_NEW:
                return new RGBColor(255, 255, 255);
            case ADDED:
                return new RGBColor(34, 139, 34);
            case REMOVED:
                return new RGBColor(165, 42, 42);
            default:
                assert false;
                return new RGBColor(255, 255, 255);
        }
    }

    public MergedColorIndex(MergeCfgBridge mergeCfgBridge) {
        super();
        colorList.add(new RGBColor(0, 0, 0)); // 0
        colorList.add(new RGBColor(0, 0, 255)); // 1
        colorList.add(new RGBColor(132, 112, 255)); // 2
        colorList.add(new RGBColor(255, 255, 255)); // 3
        colorList.add(new RGBColor(34, 139, 34)); // 4
        colorList.add(new RGBColor(165, 42, 42)); // 5

        // colorList.add( new Color(221,160,221) );	// 2
        // colorList.add( new Color(148,0,211) );		// 3
        this.mergeCfgBridge = mergeCfgBridge;
    }

    @Override
    public RGBColor get(int i) {
        return colorForProposalState(this.mergeCfgBridge.getLastProposalStateForIndex(i));
        // colorList.get(i);
    }

    @Override
    public int numUniqueColors() {
        return colorList.size();
    }

    @Override
    public boolean has(int i) {
        return i < mergeCfgBridge.size();
    }
}
