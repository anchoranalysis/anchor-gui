/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import java.util.List;
import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainerFromList;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.threaded.indexable.InternalFrameThreadedIndexableRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

public class InternalFrameMultiRaster {

    private InternalFrameThreadedIndexableRaster delegate;

    public InternalFrameMultiRaster(String frameName) {
        delegate = new InternalFrameThreadedIndexableRaster(frameName);
    }

    public ISliderState init(
            List<NamedRasterSet> list,
            DefaultModuleState initialState,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        assert (mpg.getLogger() != null);

        BoundedIndexContainerBridgeWithoutIndex<
                        NamedRasterSet, DisplayStack, BridgeElementException>
                bridge =
                        new BoundedIndexContainerBridgeWithoutIndex<>(
                                new BoundedIndexContainerFromList<>(list),
                                InternalFrameMultiRaster::convertToDisplayStack);

        ISliderState sliderState =
                delegate.init(bridge, initialState, false, elementRetriever, mpg);

        delegate.addAdditionalDetails(index -> String.format("id=%s", list.get(index).getName()));

        AddBackgroundPopup.apply(
                delegate.controllerPopupMenu(),
                delegate.backgroundSetter(),
                list,
                sliderState,
                mpg);

        return sliderState;
    }

    private static DisplayStack convertToDisplayStack(NamedRasterSet set)
            throws BridgeElementException {
        try {
            return ConvertToDisplayStack.apply(set);
        } catch (OperationFailedException e) {
            throw new BridgeElementException(e);
        }
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
