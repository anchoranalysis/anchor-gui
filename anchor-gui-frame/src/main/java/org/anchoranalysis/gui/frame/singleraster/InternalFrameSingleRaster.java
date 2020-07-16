/* (C)2020 */
package org.anchoranalysis.gui.frame.singleraster;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IntegerSequenceContaner;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.threaded.indexable.InternalFrameThreadedIndexableRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

public class InternalFrameSingleRaster {

    private InternalFrameThreadedIndexableRaster delegate;

    private ControllerPopupMenuWithBackground controller;

    public InternalFrameSingleRaster(String frameName) {
        delegate = new InternalFrameThreadedIndexableRaster(frameName);
    }

    public ISliderState init(
            int numFrames, DefaultModuleState initialState, VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // Create a sequence from 0 to numFrames -1, and map to our bridge
        BoundedIndexContainerBridgeWithoutIndex<Integer, DisplayStack, GetOperationFailedException>
                bridge =
                        new BoundedIndexContainerBridgeWithoutIndex<>(
                                new IntegerSequenceContaner(numFrames),
                                initialState.getLinkState().getBackground());

        ISliderState sliderState =
                delegate.init(bridge, initialState, true, delegate.getElementRetriever(), mpg);

        // Let's switch off the index-bar if we only have a single frame
        if (numFrames == 1) {
            delegate.setIndexSliderVisible(false);
        }

        controller =
                new ControllerPopupMenuWithBackground(
                        delegate.controllerPopupMenu(), delegate.backgroundSetter());

        return sliderState;
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
        return controller;
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
