/* (C)2020 */
package org.anchoranalysis.gui.mdi;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class PartitionedFrameList {
    private ArrayList<JComponent> fixedSizeFrameList = new ArrayList<>();
    private ArrayList<JComponent> dynamicSizeFrameList = new ArrayList<>();

    public List<JComponent> getFixedFrames() {
        return fixedSizeFrameList;
    }

    public List<JComponent> getDynamicFrames() {
        return dynamicSizeFrameList;
    }
}
