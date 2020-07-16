/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.gui.displayupdate.IOverlayedImgStackProvider;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;

class TitleBoundsUpdater implements ChangeListener {

    private ErrorReporter errorReporter;
    private IIndexGettableSettable indexCntr;
    private IOverlayedImgStackProvider stackProvider;
    private SliceIndexSlider slider;
    private InternalFrameDelegate frame;
    private String frameName;

    public TitleBoundsUpdater(
            ErrorReporter errorReporter,
            IIndexGettableSettable indexCntr,
            IOverlayedImgStackProvider stackProvider,
            SliceIndexSlider slider,
            InternalFrameDelegate frame,
            String frameName) {
        super();
        this.errorReporter = errorReporter;
        this.indexCntr = indexCntr;
        this.stackProvider = stackProvider;
        this.slider = slider;
        this.frame = frame;
        this.frameName = frameName;
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        // System.out.println("InternalFrameCanvas:StackProviderChanged:stateChanged start");

        // Just in case our slice bounds change as the provided image changes
        try {
            updateSliceBounds();
        } catch (OperationFailedException e1) {
            errorReporter.recordError(TitleBoundsUpdater.class, e1);
        }
        updateTitle();
    }

    // Maybe this gets called before init
    public void updateSliceBounds() throws OperationFailedException {
        try {
            BoundOverlayedDisplayStack initialStack = this.stackProvider.getCurrentDisplayStack();
            ChnlSliceRange sliceBounds = new ChnlSliceRange(initialStack.getDimensions());

            if (slider == null) {
                return;
            }

            slider.setSliceBounds(sliceBounds);
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    private String genTitle(int iter) {
        if (slider.getIndexSliderVisible()) {
            return new FrameTitleGenerator().genTitleString(this.frameName, iter);
        } else {
            return new FrameTitleGenerator().genTitleString(this.frameName);
        }
    }

    public void updateTitle() {
        String titleStr = genTitle(indexCntr.getIndex());
        // System.out.println("InternalFrameCanvas:StackProviderChanged:stateChanged:updateTitle:setTitle start");
        frame.setTitle(titleStr);
        // System.out.println("InternalFrameCanvas:StackProviderChanged:stateChanged:updateTitle:setTitle end");
    }
}
