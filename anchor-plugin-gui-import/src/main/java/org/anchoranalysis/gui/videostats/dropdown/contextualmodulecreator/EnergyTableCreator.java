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

package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.marks.StatePanelFrameHistoryMarks;
import org.anchoranalysis.gui.marks.table.MarksEnergyTablePanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyStackSupplier;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

@AllArgsConstructor
public class EnergyTableCreator extends VideoStatsModuleCreatorContext {

    private final CheckedSupplier<BoundedIndexContainer<IndexableMarksWithEnergy>, OperationFailedException>
            operation;
    private final EnergyStackSupplier energyStackWithParams;
    private final ColorIndex colorIndex;

    @Override
    public boolean precondition() {
        return (colorIndex != null && energyStackWithParams != null);
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        try {
            BoundedIndexContainer<IndexableMarksWithEnergy> container = operation.get();

            StatePanelFrameHistoryMarks frame =
                    new StatePanelFrameHistoryMarks(namePrefix, true);
            frame.init(
                    defaultStateManager.getLinkStateManager().getState().getFrameIndex(),
                    container,
                    new MarksEnergyTablePanel(colorIndex, energyStackWithParams.get()),
                    mpg.getLogger().errorReporter());
            frame.controllerSize().configureSize(300, 600, 300, 1000);
            return Optional.of(frame.moduleCreator());

        } catch (IllegalArgumentException
                | InitException
                | OperationFailedException
                | GetOperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return "Energy Table";
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.empty();
    }
}
