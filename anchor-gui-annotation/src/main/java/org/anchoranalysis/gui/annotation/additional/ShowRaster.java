/* (C)2020 */
package org.anchoranalysis.gui.annotation.additional;

import java.nio.file.Path;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
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

public class ShowRaster {

    private IAddVideoStatsModule adder;
    private VideoStatsModuleGlobalParams mpg;

    public ShowRaster(IAddVideoStatsModule adder, VideoStatsModuleGlobalParams mpg) {
        super();
        this.adder = adder;
        this.mpg = mpg;
    }

    public void openAndShow(String rasterName, final Path rasterPath, RasterReader rasterReader)
            throws InitException, GetOperationFailedException {

        OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                opCreateBackgroundSet =
                        pr -> {
                            try (OpenedRaster or = rasterReader.openFile(rasterPath)) {
                                TimeSequence ts = or.open(0, pr);

                                Stack stack = ts.get(0);

                                BackgroundSet backgroundSet = new BackgroundSet();
                                backgroundSet.addItem("Associated Raster", stack);

                                return backgroundSet;

                            } catch (RasterIOException | OperationFailedException e) {
                                throw new GetOperationFailedException(e);
                            }
                        };
        show(opCreateBackgroundSet, rasterName);
    }

    public void show(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    opCreateBackgroundSet,
            String rasterName) {
        try {
            DefaultModuleState defaultModuleState =
                    adder.getSubgroup()
                            .getDefaultModuleState()
                            .copyChangeBackground(
                                    opCreateBackgroundSet
                                            .doOperation(ProgressReporterNull.get())
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

        } catch (InitException | GetOperationFailedException | VideoStatsModuleCreateException e) {
            mpg.getLogger().errorReporter().recordError(AnnotatorModuleCreator.class, e);
        }
    }
}
