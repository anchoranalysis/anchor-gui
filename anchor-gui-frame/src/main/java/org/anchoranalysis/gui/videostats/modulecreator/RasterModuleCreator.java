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

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.frame.singleraster.InternalFrameSingleRaster;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.gui.videostats.operation.combine.VideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;

public class RasterModuleCreator extends VideoStatsModuleCreator {

    private final EnergyBackground energyBackground;
    private final String fileDscr;
    private final String frameName;
    private final VideoStatsModuleGlobalParams mpg;

    private VideoStatsOperationCombine combiner =
            new VideoStatsOperationCombine() {

                @Override
                public Optional<OverlayCollectionSupplier<MarkCollection>> getMarks() {
                    return Optional.empty();
                }

                @Override
                public String generateName() {
                    return fileDscr;
                }

                @Override
                public Optional<OverlayCollectionSupplier<ObjectCollection>> getObjects() {
                    return Optional.empty();
                }

                @Override
                public EnergyBackground getEnergyBackground() {
                    return energyBackground;
                }
            };

    public RasterModuleCreator(
            EnergyBackground energyBackground,
            String fileDscr,
            String frameName,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.fileDscr = fileDscr;
        this.energyBackground = energyBackground;
        this.frameName = frameName;
        this.mpg = mpg;
    }

    @Override
    public void createAndAddVideoStatsModule(AddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            InternalFrameSingleRaster imageFrame =
                    new InternalFrameSingleRaster(String.format("%s: %s", fileDscr, frameName));
            SliderState sliderState =
                    imageFrame.init(
                            energyBackground.numberFrames(),
                            adder.getSubgroup().getDefaultModuleState().getState(),
                            mpg);

            imageFrame.controllerBackgroundMenu().add(mpg, energyBackground.getBackgroundSet());

            adder.addVideoStatsModule(
                    imageFrame
                            .moduleCreator(sliderState)
                            .createVideoStatsModule(
                                    adder.getSubgroup().getDefaultModuleState().getState()));

        } catch (InitException | OperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public Optional<VideoStatsOperationCombine> getCombiner() {
        return Optional.of(combiner);
    }
}
