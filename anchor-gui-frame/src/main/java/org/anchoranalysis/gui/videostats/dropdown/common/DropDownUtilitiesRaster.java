/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.RasterModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

public class DropDownUtilitiesRaster {

    // Note, adds as default
    public static void addRaster(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            NRGBackgroundAdder<?> nrgBackground,
            String name,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        RasterModuleCreator creator =
                new RasterModuleCreator(
                        nrgBackground.getNRGBackground(), delegate.getName(), name, mpg);

        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(nrgBackground.getAdder(), creator);
        if (addAsDefault) {
            menu.addAsDefault(
                    new VideoStatsOperationFromCreatorAndAdder(
                            name, creatorAndAdder, mpg.getThreadPool(), mpg.getLogger()));
        } else {
            menu.add(
                    new VideoStatsOperationFromCreatorAndAdder(
                            name, creatorAndAdder, mpg.getThreadPool(), mpg.getLogger()));
        }
    }
}
