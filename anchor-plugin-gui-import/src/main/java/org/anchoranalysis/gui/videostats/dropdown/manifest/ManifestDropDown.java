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
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.core.cache.CacheCall;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CallableWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.CallableWithProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksCombine;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromFolder;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromNrgStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacksFromRootFiles;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.io.loader.manifest.finder.CfgNRGFinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolderKernelIterDescription;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.GuessNRGStackFromStacks;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.NRGTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.KernelIterDescriptionModuleCreator;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
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
                        manifests.descriptiveName(), "/toolbarIcon/rectangle.png");
    }

    private OperationCreateBackgroundSetWithAdder createBackgroundSetWithNRG(
            FinderNrgStack finderNrgStack,
            FinderStacksCombine finderStacks,
            IAddVideoStatsModule adder,
            VideoStatsModuleGlobalParams mpg) {

        CallableWithProgressReporter<NamedProvider<Stack>, OperationFailedException> opStacks =
                finderStacks.getStacksAsOperation();

        // We try to read a nrgStack from the manifest. If none exists, we guess instead from the
        // image-stacks.
        CallableWithProgressReporter<NRGStackWithParams, GetOperationFailedException> opNrg =
                new OperationReplaceNull<>(
                        finderNrgStack.operationNrgStackWithProgressReporter(),
                        new GuessNRGStackFromStacks(asSequence(opStacks)));

        NRGBackground nrgBackground = NRGBackground.createStack(opStacks, opNrg);

        return new OperationCreateBackgroundSetWithAdder(
                nrgBackground, adder, mpg.getThreadPool(), mpg.getLogger().errorReporter());
    }

    private CallableWithProgressReporter<TimeSequenceProvider, CreateException> asSequence(
            CallableWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
                    opStacks) {
        return pr -> {
            try {
                return new TimeSequenceProvider(
                        new WrapStackAsTimeSequence(opStacks.call(pr)), 1);
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        };
    }

    private FinderStacksCombine createFinderStacks(
            FinderNrgStack finderNrgStack, RasterReader rasterReader) throws InitException {
        // Finders
        FinderStacksCombine combined =
                new FinderStacksCombine();
        combined.add(
                new FinderStacksFromFolder(rasterReader, "stackCollection"));
        combined.add(
                new FinderStacksFromRootFiles(rasterReader, "out"));
        combined.add(
                new FinderStacksFromNrgStack(
                        finderNrgStack)); // Adds the NRG stack to the input stack collection

        // Disabled, as they not be the same size as the existing image
        combined.add(
                new FinderStacksFromRootFiles(rasterReader, "stackFromCollection"));
        combined.add(
                new FinderStacksFromFolder(rasterReader, "chnlScaledCollection"));

        try {
            if (!combined.doFind(manifests.getFileManifest().call())) {
                throw new InitException("cannot find image stack collection");
            }
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }

        return combined;
    }

    private FinderNrgStack createFinderNrgStack(
            RasterReader rasterReader, VideoStatsModuleGlobalParams mpg) throws InitException {
        FinderNrgStack finderNrgStack =
                new FinderNrgStack(rasterReader, mpg.getLogger().errorReporter());
        try {
            finderNrgStack.doFind(manifests.getFileManifest().call());
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        return finderNrgStack;
    }

    private FinderSerializedObject<KernelProposer<CfgNRGPixelized>> createFinderKernelProposer(
            VideoStatsModuleGlobalParams mpg) {
        FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer =
                new FinderSerializedObject<>("kernelProposer", mpg.getLogger().errorReporter());
        finderKernelProposer.doFind(
                manifests.getExperimentManifest().get() // NOSONAR
                );
        return finderKernelProposer;
    }

    private FinderSerializedObject<KeyValueParams> createFinderGroupParams(
            VideoStatsModuleGlobalParams mpg) throws InitException {
        final FinderSerializedObject<KeyValueParams> finderGroupParams =
                new FinderSerializedObject<>("groupParams", mpg.getLogger().errorReporter());
        try {
            finderGroupParams.doFind(manifests.getFileManifest().call());
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        return finderGroupParams;
    }

    private FinderHistoryFolderKernelIterDescription createFinderKernelIterDescription() throws InitException {

        final FinderHistoryFolderKernelIterDescription finderKernelIterDescription =
                new FinderHistoryFolderKernelIterDescription("kernelIterDescription");
        try {
            finderKernelIterDescription.doFind(manifests.getFileManifest().call());
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        return finderKernelIterDescription;
    }

    private MarkEvaluatorSetForImage createMarkEvaluatorSet(
            MarkEvaluatorManager markEvaluatorManager,
            FinderStacksCombine finderStacks,
            FinderSerializedObject<KeyValueParams> finderGroupParams)
            throws InitException {
        try {
            return markEvaluatorManager.createSetForStackCollection(
                    finderStacks.getStacksAsOperation(),
                    finderGroupParams.operation());
        } catch (CreateException e1) {
            throw new InitException(e1);
        }
    }

    public void init(
            IAddVideoStatsModule adder,
            RasterReader rasterReader,
            MarkEvaluatorManager markEvaluatorManager,
            BoundOutputManagerRouteErrors outputManager,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // Some necessary Finders and Objects
        FinderNrgStack finderNrgStack = createFinderNrgStack(rasterReader, mpg);
        FinderStacksCombine finderStacks =
                createFinderStacks(finderNrgStack, rasterReader);
        OperationCreateBackgroundSetWithAdder opBackgroundNRG =
                createBackgroundSetWithNRG(finderNrgStack, finderStacks, adder, mpg);

        // Add: Cfgs, Rasters and object-masks
        boolean defaultAdded = addCfgs(opBackgroundNRG, finderNrgStack, mpg);
        addRaster(opBackgroundNRG, mpg, defaultAdded);
        new AddObjects(delegate, manifests, finderNrgStack, mpg, markDisplaySettings)
                .apply(opBackgroundNRG);

        // Various useful finders
        FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer =
                createFinderKernelProposer(mpg);

        FinderSerializedObject<KeyValueParams> finderGroupParams = createFinderGroupParams(mpg);
        FinderHistoryFolderKernelIterDescription finderKernelIterDescription = createFinderKernelIterDescription();

        addKernelHistoryNavigator(
                finderKernelIterDescription, finderKernelProposer, opBackgroundNRG, mpg);

        outputManager =
                DropDownUtilities.createOutputManagerForSubfolder(
                        outputManager, delegate.getName());

        // Proposer Evaluators
        MarkEvaluatorSetForImage markEvaluatorSet =
                createMarkEvaluatorSet(
                        markEvaluatorManager, finderStacks, finderGroupParams);
        addProposerEvaluator(
                finderStacks,
                finderNrgStack,
                markEvaluatorSet,
                outputManager,
                adder,
                mpg);

        delegate.getRootMenu().addSeparator();

        addSetsWrap(
                finderNrgStack,
                finderStacks,
                opBackgroundNRG,
                finderKernelProposer,
                outputManager,
                adder,
                mpg);
    }

    private void addSetsWrap(
            FinderNrgStack finderNrgStack,
            FinderStacksCombine finderStacks,
            OperationCreateBackgroundSetWithAdder opBackgroundNRG,
            FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
            BoundOutputManagerRouteErrors outputManager,
            IAddVideoStatsModule adder,
            VideoStatsModuleGlobalParams mpg) {
        try {
            CfgNRGFinderContext context =
                    new CfgNRGFinderContext(
                            finderStacks,
                            finderKernelProposer,
                            adder.getParentFrame(),
                            outputManager,
                            mpg);

            AddSets addSets = new AddSets(delegate, opBackgroundNRG, context);

            addSets.apply(manifests, finderNrgStack);

        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }
    }

    private void addProposerEvaluator(
            FinderStacksCombine finderStacks,
            FinderNrgStack finderNrgStack,
            MarkEvaluatorSetForImage markEvaluatorSet,
            BoundOutputManagerRouteErrors outputManager,
            IAddVideoStatsModule adder,
            VideoStatsModuleGlobalParams mpg) {
        OperationCreateBackgroundSetWithAdder operationBwsa =
                new OperationCreateBackgroundSetWithAdder(
                        NRGBackground.createStack(
                                finderStacks
                                        .getStacksAsOperation(),
                                finderNrgStack.operationNrgStackWithProgressReporter()),
                        adder,
                        mpg.getThreadPool(),
                        mpg.getLogger().errorReporter());

        // TODO make this quicker
        // We only need specific images for the MarkProposer not the entire background set
        DropDownUtilities.addAllProposerEvaluator(
                delegate,
                operationBwsa.operationAdder(),
                operationBwsa.nrgBackground().getBackground().getBackgroundSet(),
                markEvaluatorSet,
                outputManager.getOutputWriteSettings(),
                true,
                mpg);
    }

    private void addKernelHistoryNavigator(
            FinderHistoryFolderKernelIterDescription finderKernelIterDescription,
            FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
            OperationCreateBackgroundSetWithAdder opBackgroundNRG,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {
        if (finderKernelIterDescription.exists() && finderKernelProposer.exists()) {
            try {
                delegate.addModule(
                        opBackgroundNRG.operationAdder(),
                        new SingleContextualModuleCreator(
                                new KernelIterDescriptionModuleCreator(
                                        finderKernelIterDescription, finderKernelProposer)),
                        "Kernel History Navigator",
                        mpg);
            } catch (MenuAddException e) {
                throw new InitException(e);
            }
        }
    }

    private void addRaster(
            OperationCreateBackgroundSetWithAdder opBackground,
            VideoStatsModuleGlobalParams mpg,
            boolean defaultAdded) {
        DropDownUtilitiesRaster.addRaster(
                delegate.getRootMenu(),
                delegate,
                opBackground.nrgBackground(),
                "Raster",
                mpg,
                !defaultAdded // Adds as default operation
                );
    }

    // Returns TRUE if it's added a default, FALSE otherwise
    private boolean addCfgs(
            OperationCreateBackgroundSetWithAdder operationBwsaWithNRG,
            FinderNrgStack finderNrgStack,
            VideoStatsModuleGlobalParams mpg) {

        boolean defaultAdded = false;

        try {
            final FinderSerializedObject<Cfg> finderFinalCfg =
                    new FinderSerializedObject<>("cfg", mpg.getLogger().errorReporter());
            finderFinalCfg.doFind(manifests.getFileManifest().call());

            if (finderFinalCfg.exists()) {

                DropDownUtilities.addCfg(
                        delegate.getRootMenu(),
                        delegate,
                        getCfgChangeException(finderFinalCfg),
                        "Final Cfg",
                        operationBwsaWithNRG.nrgBackground(),
                        mpg,
                        markDisplaySettings,
                        true);
                defaultAdded = true;
            }
        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }

        if (addNRGTable(operationBwsaWithNRG, finderNrgStack, mpg)) {
            defaultAdded = true;
        }

        addCfgSubMenu(operationBwsaWithNRG, mpg);
        return defaultAdded;
    }

    private CallableWithException<Cfg, OperationFailedException> getCfgChangeException(
            FinderSerializedObject<Cfg> finderFinalCfg) {
        return () -> {
            try {
                return finderFinalCfg.operation().call().get();
            } catch (IOException e) {
                throw new OperationFailedException(e);
            }
        };
    }

    private boolean addNRGTable(
            OperationCreateBackgroundSetWithAdder operationBwsaWithNRG,
            FinderNrgStack finderNrgStack,
            VideoStatsModuleGlobalParams mpg) {
        try {
            final FinderSerializedObject<CfgNRG> finderFinalCfgNRG =
                    new FinderSerializedObject<>("cfgNRG", mpg.getLogger().errorReporter());
            finderFinalCfgNRG.doFind(manifests.getFileManifest().call());

            if (finderFinalCfgNRG.exists()) {

                CacheCall<LoadContainer<CfgNRGInstantState>, OperationFailedException> op =
                        CacheCall.of(
                                () -> {
                                    CfgNRGInstantState instantState;
                                    try {
                                        instantState =
                                                new CfgNRGNonHandleInstantState(
                                                        0, finderFinalCfgNRG.get());
                                    } catch (IOException e) {
                                        throw new OperationFailedException(e);
                                    }

                                    LoadContainer<CfgNRGInstantState> lc = new LoadContainer<>();
                                    lc.setExpensiveLoad(false);
                                    lc.setCntr(new SingleContainer<>(instantState, 0, false));
                                    return lc;
                                });

                if (finderNrgStack.exists()) {
                    // NRG Table
                    SingleContextualModuleCreator creator =
                            new SingleContextualModuleCreator(
                                    new NRGTableCreator(
                                            op,
                                            finderNrgStack.operationNrgStack(),
                                            mpg.getDefaultColorIndexForMarks()));
                    delegate.addModule(
                            operationBwsaWithNRG.operationAdder(), creator, "NRG Table", mpg);
                }

                return true;
            }
        } catch (MenuAddException | OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }
        return false;
    }

    private void addCfgSubMenu(
            OperationCreateBackgroundSetWithAdder operationBwsaWithNRG,
            VideoStatsModuleGlobalParams mpg) {
        try {
            FinderCfgFolder finder = new FinderCfgFolder("cfgCollection", "cfg");
            finder.doFind(manifests.getFileManifest().call());

            NamedProvider<Cfg> provider = finder.createNamedProvider(false);
            DropDownUtilities.addCfgSubmenu(
                    delegate.getRootMenu(),
                    delegate,
                    provider,
                    operationBwsaWithNRG.nrgBackground(),
                    mpg,
                    markDisplaySettings,
                    false);
        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }
    }

    public IOpenedFileGUI openedFileGUI() {
        return delegate.openedFileGUI();
    }

    public String getName() {
        return delegate.getName();
    }
}
