/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameCfgNRGHistoryFolder;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ColoredOutlineCreator extends VideoStatsModuleCreatorContext {

    private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
    private final OperationWithProgressReporter<BackgroundSet, OperationFailedException>
            backgroundSet;

    @Override
    public boolean precondition() {
        return finderCfgNRGHistory.exists();
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        InternalFrameCfgNRGHistoryFolder imageFrame =
                new InternalFrameCfgNRGHistoryFolder(namePrefix);
        try {
            ISliderState sliderState =
                    imageFrame.init(
                            finderCfgNRGHistory.get(),
                            defaultStateManager.getState(),
                            backgroundSet,
                            mpg);

            return Optional.of(imageFrame.moduleCreator(sliderState));

        } catch (InitException | OperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return "Colored Outline";
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.empty();
    }
}
