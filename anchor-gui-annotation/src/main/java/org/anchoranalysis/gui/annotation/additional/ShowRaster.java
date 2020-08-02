/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.annotation.additional;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.CallableWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.singleraster.InternalFrameSingleRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

@AllArgsConstructor
public class ShowRaster {

    private final IAddVideoStatsModule adder;
    private final VideoStatsModuleGlobalParams mpg;

    public void openAndShow(String rasterName, final Path rasterPath, RasterReader rasterReader) {

        show(
                pr -> {
                    try (OpenedRaster or = rasterReader.openFile(rasterPath)) {
                        TimeSequence ts = or.open(0, pr);

                        Stack stack = ts.get(0);

                        BackgroundSet backgroundSet = new BackgroundSet();
                        backgroundSet.addItem("Associated Raster", stack);

                        return backgroundSet;

                    } catch (RasterIOException | OperationFailedException e) {
                        throw new BackgroundStackContainerException(rasterName, e);
                    }
                },
                rasterName);
    }

    public void show(
            CallableWithProgressReporter<BackgroundSet, BackgroundStackContainerException>
                    opCreateBackgroundSet,
            String rasterName) {
        try {
            DefaultModuleState defaultModuleState =
                    adder.getSubgroup()
                            .getDefaultModuleState()
                            .copyChangeBackground(
                                    opCreateBackgroundSet
                                            .call(ProgressReporterNull.get())
                                            .stackCntr("Associated Raster"));

            InternalFrameSingleRaster imageFrame = new InternalFrameSingleRaster(rasterName);
            ISliderState sliderState = imageFrame.init(1, defaultModuleState, mpg);

            imageFrame
                    .controllerBackgroundMenu()
                    .addDefinition(
                            mpg, new ChangeableBackgroundDefinitionSimple(opCreateBackgroundSet));

            adder.addVideoStatsModule(
                    imageFrame
                            .moduleCreator(sliderState)
                            .createVideoStatsModule(
                                    adder.getSubgroup().getDefaultModuleState().getState()));

        } catch (InitException
                | VideoStatsModuleCreateException
                | BackgroundStackContainerException
                | GetOperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(AnnotatorModuleCreator.class, e);
        }
    }
}
