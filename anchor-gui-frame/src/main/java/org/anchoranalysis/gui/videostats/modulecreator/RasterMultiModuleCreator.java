/* (C)2020 */
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
