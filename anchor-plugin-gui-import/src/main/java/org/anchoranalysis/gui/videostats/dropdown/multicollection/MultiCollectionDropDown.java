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

package org.anchoranalysis.gui.videostats.dropdown.multicollection;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.series.TimeSequenceProviderSupplier;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.dropdown.common.GuessEnergyFromStacks;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.wrap.WrapTimeSequenceAsStack;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class MultiCollectionDropDown {

    private BoundVideoStatsModuleDropDown delegate;

    private TimeSequenceProviderSupplier rasterProvider;
    private NamedProvider<MarkCollection> marksCollection;
    private NamedProvider<ObjectCollection> objCollection;
    private NamedProviderStore<KeyValueParams> paramsCollection;
    private boolean addProposerEvaluator;

    // A dropdown menu representing a particular manifest
    public MultiCollectionDropDown(
            TimeSequenceProviderSupplier rasterProvider,
            NamedProvider<MarkCollection> marksCollection,
            NamedProvider<ObjectCollection> objCollection,
            NamedProviderStore<KeyValueParams> paramsCollection,
            String name,
            boolean addProposerEvaluator) {
        this.rasterProvider = rasterProvider;
        this.marksCollection = marksCollection;
        this.paramsCollection = paramsCollection;
        this.delegate = new BoundVideoStatsModuleDropDown(name, "/toolbarIcon/rectangle.png");
        this.addProposerEvaluator = addProposerEvaluator;
        this.objCollection = objCollection;
    }

    public void init(final AddVideoStatsModule adder, InputOutputContext context, MarkCreatorParams params)
            throws InitException {

        OperationCreateBackgroundSetWithAdder operationBwsa =
                new OperationCreateBackgroundSetWithAdder(
                        EnergyBackground.createStackSequence(
                                rasterProvider, () -> GuessEnergyFromStacks.guess(rasterProvider)),
                        adder,
                        params.getModuleParams().getThreadPool(),
                        params.getModuleParams().getLogger().errorReporter());

        DropDownUtilitiesRaster.addRaster(
                delegate.getRootMenu(),
                delegate,
                operationBwsa.energyBackground(),
                "Raster",
                params.getModuleParams(),
                true // Adds as default operation
                );

        if (marksCollection != null) {
            DropDownUtilities.addMarksSubmenu(
                    delegate.getRootMenu(),
                    delegate,
                    marksCollection,
                    operationBwsa.energyBackground(),
                    params.getModuleParams(),
                    params.getMarkDisplaySettings(),
                    false);
        }

        if (objCollection != null) {
            DropDownUtilities.addObjectsSubmenu(
                    delegate.getRootMenu(),
                    delegate,
                    objCollection,
                    operationBwsa.energyBackground(),
                    params.getModuleParams(),
                    false);
        }

        if (addProposerEvaluator) {
            addProposerEvaluator(
                    params.getMarkEvaluatorManager(),
                    operationBwsa,
                    params.getModuleParams(),
                    context);
        }
    }

    private void addProposerEvaluator(
            MarkEvaluatorManager markEvaluatorManager,
            OperationCreateBackgroundSetWithAdder operationBwsa,
            VideoStatsModuleGlobalParams mpg,
            InputOutputContext context)
            throws InitException {

        Outputter outputterSubdirectory =
                DropDownUtilities.createSubdirectoryContext(context, delegate.getName()).getOutputter();

        try {
            MarkEvaluatorSetForImage markEvaluatorSet =
                    markEvaluatorManager.createSetForStackCollection(
                            this::extractFromRasterProvider, () -> extractParams(mpg));

            // If we have a markEvaluator, then we add some extra menus
            if (markEvaluatorSet.hasItems()) {

                // TODO make this quicker
                // We only need specific images for the MarkProposer not the entire background set
                DropDownUtilities.addAllProposerEvaluator(
                        delegate,
                        operationBwsa.operationAdder(),
                        operationBwsa.energyBackground().getBackground().getBackgroundSet(),
                        markEvaluatorSet,
                        outputterSubdirectory.getSettings(),
                        true,
                        mpg);
            }

        } catch (CreateException e) {
            throw new InitException(e);
        }
    }

    public OpenedFileGUI openedFileGUI() {
        return delegate.openedFileGUI();
    }

    public String getName() {
        return delegate.getName();
    }

    private WrapTimeSequenceAsStack extractFromRasterProvider(ProgressReporter progressReporter)
            throws OperationFailedException {
        try {
            return new WrapTimeSequenceAsStack(rasterProvider.get(progressReporter).getSequence());
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    private Optional<KeyValueParams> extractParams(VideoStatsModuleGlobalParams mpg) {
        return Optional.of(ParamsUtils.apply(paramsCollection, mpg.getLogger().errorReporter()));
    }
}
