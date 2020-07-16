/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

public class InitialSliderState {

    private boolean frameAdjusting;
    private int index;
    private int sliceNum;
    private boolean showIndexSlider;

    public InitialSliderState(
            boolean frameAdjusting, int index, int sliceNum, boolean showIndexSlider) {
        super();
        this.frameAdjusting = frameAdjusting;
        this.index = index;
        this.sliceNum = sliceNum;
        this.showIndexSlider = showIndexSlider;
    }

    public boolean isFrameAdjusting() {
        return frameAdjusting;
    }

    public int getIndex() {
        return index;
    }

    public int getSliceNum() {
        return sliceNum;
    }

    public boolean isShowIndexSlider() {
        return showIndexSlider;
    }
}
