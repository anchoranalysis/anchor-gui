/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.IImageStackCntrFromName;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

class AddBackgroundPopup {

    public static void apply(
            ControllerPopupMenu popUpMenu,
            IBackgroundSetter backgroundSetter,
            List<NamedRasterSet> list,
            ISliderState sliderState,
            VideoStatsModuleGlobalParams mpg) {
        ControllerPopupMenuWithBackground controller =
                new ControllerPopupMenuWithBackground(popUpMenu, backgroundSetter);
        controller.add(createGetNames(list, sliderState, mpg), stackCntrFromName(list), mpg);
    }

    private static IImageStackCntrFromName stackCntrFromName(List<NamedRasterSet> list) {
        return name ->
                sourceObject -> {
                    return list.get(sourceObject)
                            .getBackgroundSet()
                            .doOperation(ProgressReporterNull.get())
                            .singleStack(name);
                };
    }

    private static IGetNames createGetNames(
            List<NamedRasterSet> list, ISliderState sliderState, VideoStatsModuleGlobalParams mpg) {
        return () -> {
            try {
                Set<String> names =
                        list.get(sliderState.getIndex())
                                .getBackgroundSet()
                                .doOperation(ProgressReporterNull.get())
                                .names();
                return new ArrayList<>(names);

            } catch (Throwable e) {
                mpg.getLogger().errorReporter().recordError(InternalFrameMultiRaster.class, e);
                return new ArrayList<>();
            }
        };
    }
}
