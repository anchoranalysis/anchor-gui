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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.BackgroundSetter;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class AddBackgroundPopup {

    public static void apply(
            ControllerPopupMenu popUpMenu,
            BackgroundSetter backgroundSetter,
            List<NamedRasterSet> list,
            SliderState sliderState,
            VideoStatsModuleGlobalParams mpg) {
        ControllerPopupMenuWithBackground controller =
                new ControllerPopupMenuWithBackground(popUpMenu, backgroundSetter);
        controller.add(createGetNames(list, sliderState, mpg), stackCntrFromName(list), mpg);
    }

    private static ImageStackContainerFromName stackCntrFromName(List<NamedRasterSet> list) {
        return name ->
                sourceObject -> {
                    return list.get(sourceObject)
                            .getBackgroundSet()
                            .get(ProgressReporterNull.get())
                            .singleStack(name);
                };
    }

    private static IGetNames createGetNames(
            List<NamedRasterSet> list, SliderState sliderState, VideoStatsModuleGlobalParams mpg) {
        return () -> {
            try {
                Set<String> names =
                        list.get(sliderState.getIndex())
                                .getBackgroundSet()
                                .get(ProgressReporterNull.get())
                                .names();
                return new ArrayList<>(names);

            } catch (Exception e) {
                mpg.getLogger().errorReporter().recordError(InternalFrameMultiRaster.class, e);
                return new ArrayList<>();
            }
        };
    }
}
