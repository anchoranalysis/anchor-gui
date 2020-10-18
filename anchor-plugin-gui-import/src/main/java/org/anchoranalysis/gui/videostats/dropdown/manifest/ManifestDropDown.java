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

package org.anchoranalysis.gui.videostats.dropdown.manifest;

import java.io.IOException;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.finder.FinderEnergyStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksCombine;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromEnergyStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromFolder;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromRootFiles;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderMarksFolder;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.series.TimeSequenceProviderSupplier;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyStackSupplier;
import org.anchoranalysis.gui.videostats.dropdown.common.GuessEnergyFromStacks;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.EnergyTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.image.core.stack.NamedStacksSupplier;
import org.anchoranalysis.image.core.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.io.manifest.finder.FindFailedException;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.feature.energy.marks.MarksWithEnergyBreakdown;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

public class ManifestDropDown {

    private BoundVideoStatsModuleDropDown delegate;

    private CoupledManifests manifests;

    private MarkDisplaySettings markDisplaySettings;

    // A dropdown menu representing a particular manifest
    public ManifestDropDown(CoupledManifests manifests, MarkDisplaySettings markDisplaySettings) {
        this.manifests = manifests;
        this.markDisplaySettings = markDisplaySettings;
        this.delegate =
                new BoundVideoStatsModuleDropDown(
                        manifests.name(), "/toolbarIcon/rectangle.png");
    }
    
    public OpenedFileGUI openedFileGUI() {
        return delegate.openedFileGUI();
    }

    public String getName() {
        return delegate.getName();
    }

    private OperationCreateBackgroundSetWithAdder createBackgroundSetWithEnergy(
            FinderEnergyStack finderEnergyStack,
            FinderStacksCombine finderStacks,
            AddVideoStatsModule adder,
            VideoStatsModuleGlobalParams mpg) {

        NamedStacksSupplier stacks = finderStacks.getStacksAsOperation();

        // We try to read a energyStack from the manifest. If none exists, we guess instead from the
        // image-stacks.
        EnergyBackground energyBackground =
                EnergyBackground.createStack(
                        stacks,
                        () ->
                                firstOrSecond(
                                        finderEnergyStack.energyStackSupplier(),
                                        () -> GuessEnergyFromStacks.guess(asSequence(stacks))));

        return new OperationCreateBackgroundSetWithAdder(
                energyBackground, adder, mpg.getThreadPool(), mpg.getLogger().errorReporter());
    }

    /** Gets from the first supplier, and if it returns null, then gets from the second instead */
    private static EnergyStack firstOrSecond(EnergyStackSupplier first, EnergyStackSupplier second)
            throws GetOperationFailedException {

        EnergyStack firstResult = first.get();

        if (firstResult == null) {
            return second.get();
        }

        return firstResult;
    }

    private TimeSequenceProviderSupplier asSequence(NamedStacksSupplier opStacks) {
        return pr -> {
            try {
                return new TimeSequenceProvider(new WrapStackAsTimeSequence(opStacks.get(pr)), 1);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        };
    }

    private FinderStacksCombine createFinderStacks(
            FinderEnergyStack finderEnergyStack, StackReader stackReader) throws InitException {
        // Finders
        FinderStacksCombine combined = new FinderStacksCombine();
        combined.add(new FinderStacksFromFolder(stackReader, OutputterDirectories.STACKS));
        combined.add(new FinderStacksFromRootFiles(stackReader, "out"));
        combined.add(
                new FinderStacksFromEnergyStack(
                        finderEnergyStack)); // Adds the energy stack to the input stack collection

        // Disabled, as they not be the same size as the existing image
        combined.add(new FinderStacksFromRootFiles(stackReader, "stackFromCollection"));
        combined.add(new FinderStacksFromFolder(stackReader, "channelScaledCollection"));

        try {
            if (!combined.doFind(manifests.getJobManifest().get())) {
                throw new InitException("cannot find image stack collection");
            }
        } catch (OperationFailedException | FindFailedException e) {
            throw new InitException(e);
        }

        return combined;
    }

    private FinderEnergyStack createFinderEnergyStack(
            StackReader stackReader, VideoStatsModuleGlobalParams mpg) throws InitException {
        FinderEnergyStack finderEnergyStack =
                new FinderEnergyStack(stackReader, mpg.getLogger().errorReporter());
        try {
            finderEnergyStack.doFind(manifests.getJobManifest().get());
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        return finderEnergyStack;
    }

    private FinderSerializedObject<KeyValueParams> createFinderGroupParams(
            VideoStatsModuleGlobalParams mpg) throws InitException {
        final FinderSerializedObject<KeyValueParams> finderGroupParams =
                new FinderSerializedObject<>("groupParams", mpg.getLogger().errorReporter());
        try {
            finderGroupParams.doFind(manifests.getJobManifest().get());
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        return finderGroupParams;
    }

    private MarkEvaluatorSetForImage createMarkEvaluatorSet(
            MarkEvaluatorManager markEvaluatorManager,
            FinderStacksCombine finderStacks,
            FinderSerializedObject<KeyValueParams> finderGroupParams)
            throws InitException {
        try {
            return markEvaluatorManager.createSetForStackCollection(
                    finderStacks.getStacksAsOperation(), finderGroupParams.getMemoized()::get);
        } catch (CreateException e1) {
            throw new InitException(e1);
        }
    }

    public void init(
            AddVideoStatsModule adder,
            StackReader stackReader,
            MarkEvaluatorManager markEvaluatorManager,
            InputOutputContext inputOutputContext,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // Some necessary Finders and Objects
        FinderEnergyStack finderEnergyStack = createFinderEnergyStack(stackReader, mpg);
        FinderStacksCombine finderStacks = createFinderStacks(finderEnergyStack, stackReader);
        OperationCreateBackgroundSetWithAdder backgroundEnergy =
                createBackgroundSetWithEnergy(finderEnergyStack, finderStacks, adder, mpg);

        // Add: Markss, Rasters and object-masks
        boolean defaultAdded = addMarks(backgroundEnergy, finderEnergyStack, mpg);
        addRaster(backgroundEnergy, mpg, defaultAdded);
        new AddObjects(delegate, manifests, finderEnergyStack, mpg, markDisplaySettings)
                .apply(backgroundEnergy);

        FinderSerializedObject<KeyValueParams> finderGroupParams = createFinderGroupParams(mpg);

        inputOutputContext = DropDownUtilities.createSubdirectoryContext(inputOutputContext, delegate.getName());

        // Proposer Evaluators
        MarkEvaluatorSetForImage markEvaluatorSet =
                createMarkEvaluatorSet(markEvaluatorManager, finderStacks, finderGroupParams);
        addProposerEvaluator(
                finderStacks, finderEnergyStack, markEvaluatorSet, inputOutputContext.getOutputter(), adder, mpg);
    }

    private void addProposerEvaluator(
            FinderStacksCombine finderStacks,
            FinderEnergyStack finderEnergyStack,
            MarkEvaluatorSetForImage markEvaluatorSet,
            Outputter outputter,
            AddVideoStatsModule adder,
            VideoStatsModuleGlobalParams mpg) {
        OperationCreateBackgroundSetWithAdder operationBwsa =
                new OperationCreateBackgroundSetWithAdder(
                        EnergyBackground.createStack(
                                finderStacks.getStacksAsOperation(),
                                finderEnergyStack.energyStackSupplier()),
                        adder,
                        mpg.getThreadPool(),
                        mpg.getLogger().errorReporter());

        // TODO make this quicker
        // We only need specific images for the MarkProposer not the entire background set
        DropDownUtilities.addAllProposerEvaluator(
                delegate,
                operationBwsa.operationAdder(),
                operationBwsa.energyBackground().getBackground().getBackgroundSet(),
                markEvaluatorSet,
                outputter.getSettings(),
                true,
                mpg);
    }

    private void addRaster(
            OperationCreateBackgroundSetWithAdder background,
            VideoStatsModuleGlobalParams mpg,
            boolean defaultAdded) {
        DropDownUtilitiesRaster.addRaster(
                delegate.getRootMenu(),
                delegate,
                background.energyBackground(),
                "Raster",
                mpg,
                !defaultAdded // Adds as default operation
                );
    }

    // Returns true if it's added a default, false otherwise
    private boolean addMarks(
            OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy,
            FinderEnergyStack finderEnergyStack,
            VideoStatsModuleGlobalParams mpg) {

        boolean defaultAdded = false;

        try {
            final FinderSerializedObject<MarkCollection> finderFinalMarks =
                    new FinderSerializedObject<>("marks", mpg.getLogger().errorReporter());
            finderFinalMarks.doFind(manifests.getJobManifest().get());

            if (finderFinalMarks.exists()) {

                DropDownUtilities.addMarks(
                        delegate.getRootMenu(),
                        delegate,
                        getMarksChangeException(finderFinalMarks),
                        "Final Marks",
                        operationBwsaWithEnergy.energyBackground(),
                        mpg,
                        markDisplaySettings,
                        true);
                defaultAdded = true;
            }
        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }

        if (addEnergyTable(operationBwsaWithEnergy, finderEnergyStack, mpg)) {
            defaultAdded = true;
        }

        addMarksSubMenu(operationBwsaWithEnergy, mpg);
        return defaultAdded;
    }

    private OverlayCollectionSupplier<MarkCollection> getMarksChangeException(
            FinderSerializedObject<MarkCollection> finderFinalMarks) {
        return () -> {
            try {
                return finderFinalMarks.get();
            } catch (IOException e) {
                throw new OperationFailedException(e);
            }
        };
    }

    private boolean addEnergyTable(
            OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy,
            FinderEnergyStack finderEnergyStack,
            VideoStatsModuleGlobalParams mpg) {
        try {
            final FinderSerializedObject<MarksWithEnergyBreakdown> finderFinalMarks =
                    new FinderSerializedObject<>("marks", mpg.getLogger().errorReporter());
            finderFinalMarks.doFind(manifests.getJobManifest().get());

            if (finderFinalMarks.exists()) {

                CachedSupplier<BoundedIndexContainer<IndexableMarksWithEnergy>, OperationFailedException>
                        op =
                                CachedSupplier.cache(
                                        () -> {
                                            IndexableMarksWithEnergy instantState;
                                            try {
                                                instantState =
                                                        new IndexableMarksWithEnergy(
                                                                0, finderFinalMarks.get());
                                            } catch (IOException e) {
                                                throw new OperationFailedException(e);
                                            }

                                            return new SingleContainer<>(instantState, 0, false);
                                        });

                if (finderEnergyStack.exists()) {
                    // Energy Table
                    SingleContextualModuleCreator creator =
                            new SingleContextualModuleCreator(
                                    new EnergyTableCreator(
                                            op,
                                            finderEnergyStack.energyStackSupplier(),
                                            mpg.getDefaultColorIndexForMarks()));
                    delegate.addModule(
                            operationBwsaWithEnergy.operationAdder(), creator, "Energy Table", mpg);
                }

                return true;
            }
        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }
        return false;
    }

    private void addMarksSubMenu(
            OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy,
            VideoStatsModuleGlobalParams mpg) {
        try {
            FinderMarksFolder finder = new FinderMarksFolder("marksCollection", "marks");
            finder.doFind(manifests.getJobManifest().get());

            NamedProvider<MarkCollection> provider = finder.createNamedProvider(false);
            DropDownUtilities.addMarksSubmenu(
                    delegate.getRootMenu(),
                    delegate,
                    provider,
                    operationBwsaWithEnergy.energyBackground(),
                    mpg,
                    markDisplaySettings,
                    false);
        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }
    }
}
