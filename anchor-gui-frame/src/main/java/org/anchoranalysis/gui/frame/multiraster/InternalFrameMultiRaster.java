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

package org.anchoranalysis.gui.frame.multiraster;

import java.util.List;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainerFromList;
import org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.threaded.indexable.InternalFrameThreadedIndexableRaster;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.core.stack.DisplayStack;

public class InternalFrameMultiRaster {

    private InternalFrameThreadedIndexableRaster delegate;

    public InternalFrameMultiRaster(String frameName) {
        delegate = new InternalFrameThreadedIndexableRaster(frameName);
    }

    public SliderState init(
            List<NamedRasterSet> list,
            DefaultModuleState initialState,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        BoundedIndexContainerBridgeWithoutIndex<
                        NamedRasterSet, DisplayStack, InitException>
                bridge =
                        new BoundedIndexContainerBridgeWithoutIndex<>(
                                new BoundedIndexContainerFromList<>(list),
                                InternalFrameMultiRaster::convertToDisplayStack);

        SliderState sliderState = delegate.init(bridge, initialState, false, elementRetriever, mpg);

        delegate.addAdditionalDetails(index -> String.format("id=%s", list.get(index).getName()));

        AddBackgroundPopup.apply(
                delegate.controllerPopupMenu(),
                delegate.backgroundSetter(),
                list,
                sliderState,
                mpg);

        return sliderState;
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }

    private static DisplayStack convertToDisplayStack(NamedRasterSet set) throws InitException {
        try {
            return ConvertToDisplayStack.apply(set);
        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }
}
