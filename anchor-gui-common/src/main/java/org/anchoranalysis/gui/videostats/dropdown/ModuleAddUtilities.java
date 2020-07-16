/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultStateSliderState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class ModuleAddUtilities {

    public static void add(IAddVideoStatsModule adder, IModuleCreatorDefaultState creator)
            throws VideoStatsModuleCreateException {
        VideoStatsModule module =
                creator.createVideoStatsModule(
                        adder.getSubgroup().getDefaultModuleState().getState());
        adder.addVideoStatsModule(module);
    }

    public static void add(
            IAddVideoStatsModule adder,
            IModuleCreatorDefaultStateSliderState creator,
            ISliderState sliderState)
            throws VideoStatsModuleCreateException {
        VideoStatsModule module =
                creator.createVideoStatsModule(
                        adder.getSubgroup().getDefaultModuleState().getState(), sliderState);
        adder.addVideoStatsModule(module);
    }
}
