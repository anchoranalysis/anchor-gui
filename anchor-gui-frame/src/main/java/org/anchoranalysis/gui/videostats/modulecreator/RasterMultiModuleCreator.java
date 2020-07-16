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

package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.frame.multiraster.InternalFrameMultiRaster;
import org.anchoranalysis.gui.frame.multiraster.NamedRasterSet;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class RasterMultiModuleCreator extends VideoStatsModuleCreator {

    private final List<NamedRasterSet> listOp;
    private final String frameName;
    private final VideoStatsModuleGlobalParams moduleParamsGlobal;

    public RasterMultiModuleCreator(
            List<NamedRasterSet> listOp,
            String frameName,
            VideoStatsModuleGlobalParams moduleParamsGlobal) {
        super();
        this.listOp = listOp;
        this.frameName = frameName;
        this.moduleParamsGlobal = moduleParamsGlobal;
        assert (moduleParamsGlobal.getExportPopupParams() != null);
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            InternalFrameMultiRaster internalFrame = new InternalFrameMultiRaster(frameName);
            ISliderState sliderState =
                    internalFrame.init(
                            listOp,
                            adder.getSubgroup().getDefaultModuleState().getState(),
                            internalFrame.getElementRetriever(),
                            moduleParamsGlobal);

            // Our menu for changing the background
            // imageFrame.addAdditionalMenu( new
            // BackgroundSetMenu(backgroundSet.doOperation(),imageFrame).getMenu() );

            VideoStatsModule module =
                    internalFrame
                            .moduleCreator(sliderState)
                            .createVideoStatsModule(
                                    adder.getSubgroup().getDefaultModuleState().getState());
            adder.addVideoStatsModule(module);

        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }
}
