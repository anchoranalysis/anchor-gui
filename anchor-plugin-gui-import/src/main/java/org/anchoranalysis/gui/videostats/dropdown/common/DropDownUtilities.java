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

package org.anchoranalysis.gui.videostats.dropdown.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModuleSupplier;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.OperationNRGStackFromMarkEvaluatorSet;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.CfgModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.ObjectCollectionModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DropDownUtilities {

    public static void addAllProposerEvaluator(
            BoundVideoStatsModuleDropDown dropDown,
            AddVideoStatsModuleSupplier adderOpWithoutNRG,
            BackgroundSetProgressingSupplier backgroundSet,
            MarkEvaluatorSetForImage markEvaluatorSet,
            OutputWriteSettings outputWriteSettings,
            boolean addNRGAdder,
            VideoStatsModuleGlobalParams mpg) {
        // Generating a NRGStackWithParams from the markEvaluatorSet
        OperationNRGStackFromMarkEvaluatorSet operationGetNRGStack =
                new OperationNRGStackFromMarkEvaluatorSet(markEvaluatorSet);

        NRGBackground nrgBackground =
                NRGBackground.createFromBackground(
                        backgroundSet, () -> operationGetNRGStack.get(ProgressReporterNull.get()));

        AddVideoStatsModuleSupplier adderOp =
                AddVideoStatsModuleSupplier.cache(
                        progressReporter -> {
                            AddVideoStatsModule adder = adderOpWithoutNRG.get(progressReporter);

                            if (addNRGAdder) {
                                nrgBackground.addNrgStackToAdder(adder);
                            }
                            return adder;
                        });

        VideoStatsModuleCreator moduleCreator =
                new ProposerEvaluatorModuleCreator(
                        markEvaluatorSet,
                        nrgBackground,
                        outputWriteSettings,
                        operationGetNRGStack,
                        mpg);

        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(adderOp, moduleCreator);
        dropDown.getRootMenu()
                .add(
                        new VideoStatsOperationFromCreatorAndAdder(
                                "Proposer Evaluator",
                                creatorAndAdder,
                                mpg.getThreadPool(),
                                mpg.getLogger()));
    }

    public static void addCfg(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            OverlayCollectionSupplier<Cfg> op,
            String name,
            NRGBackgroundAdder nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            boolean addAsDefault) {

        VideoStatsModuleCreator module =
                new CfgModuleCreator(
                        delegate.getName(),
                        name,
                        op,
                        nrgBackground.getBackground(),
                        mpg,
                        markDisplaySettings);
        addModule(module, menu, nrgBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addObjectCollection(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            OverlayCollectionSupplier<ObjectCollection> op,
            String name,
            NRGBackgroundAdder nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        VideoStatsModuleCreator module =
                new ObjectCollectionModuleCreator(
                        delegate.getName(), name, op, nrgBackground.getBackground(), mpg);
        addModule(module, menu, nrgBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addCfgSubmenu(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            NamedProvider<Cfg> cfgProvider,
            NRGBackgroundAdder nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            boolean addAsDefault) {
        if (cfgProvider.keys().isEmpty()) {
            return;
        }

        VideoStatsOperationMenu subMenu = menu.createSubMenu("Cfg", true);

        for (String providerName : cfgProvider.keys()) {

            addCfg(
                    subMenu,
                    delegate.createChild(providerName),
                    () -> getFromProvider(cfgProvider, providerName),
                    providerName,
                    nrgBackground,
                    mpg,
                    markDisplaySettings,
                    addAsDefault);
        }
    }

    public static void addObjectsSubmenu(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            final NamedProvider<ObjectCollection> provider,
            NRGBackgroundAdder nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        if (provider.keys().isEmpty()) {
            return;
        }

        VideoStatsOperationMenu subMenu = menu.createSubMenu("Objects", true);

        for (final String providerName : provider.keys()) {

            addObjectCollection(
                    subMenu,
                    delegate.createChild(providerName),
                    () -> getFromProvider(provider, providerName),
                    providerName,
                    nrgBackground,
                    mpg,
                    addAsDefault);
        }
    }

    public static BoundOutputManagerRouteErrors createOutputManagerForSubfolder(
            BoundOutputManagerRouteErrors parentOutputManager, String subFolderName)
            throws InitException {

        ManifestFolderDescription mfd =
                new ManifestFolderDescription(
                        "interactiveOutput", "manifestInteractiveOutput", new SetSequenceType());

        // NB: As bindAsSubFolder can now return nulls, maybe some knock-on bugs are introduced here
        return parentOutputManager
                .getWriterAlwaysAllowed()
                .bindAsSubdirectory(subFolderName, mfd)
                .orElseThrow(() -> new InitException("Cannot create a sub-folder for output"));
    }

    private static void addModule(
            VideoStatsModuleCreator module,
            VideoStatsOperationMenu menu,
            AddVideoStatsModuleSupplier opAdder,
            String name,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(opAdder, module);

        if (addAsDefault) {
            menu.add(
                    new VideoStatsOperationFromCreatorAndAdder(
                            name, creatorAndAdder, mpg.getThreadPool(), mpg.getLogger()));
        } else {
            menu.addAsDefault(
                    new VideoStatsOperationFromCreatorAndAdder(
                            name, creatorAndAdder, mpg.getThreadPool(), mpg.getLogger()));
        }
    }

    private static <T> T getFromProvider(NamedProvider<T> provider, String name)
            throws OperationFailedException {
        try {
            return provider.getException(name);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }
}
