/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationNRGStackFromMarkEvaluatorSet;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.CfgModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.ObjectCollectionModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.SetSequenceType;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DropDownUtilities {

    public static void addAllProposerEvaluator(
            BoundVideoStatsModuleDropDown dropDown,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable>
                    adderOpWithoutNRG,
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet,
            MarkEvaluatorSetForImage markEvaluatorSet,
            OutputWriteSettings outputWriteSettings,
            boolean addNRGAdder,
            VideoStatsModuleGlobalParams mpg) {
        // Generating a NRGStackWithParams from the markEvaluatorSet
        OperationNRGStackFromMarkEvaluatorSet operationGetNRGStack =
                new OperationNRGStackFromMarkEvaluatorSet(markEvaluatorSet);

        NRGBackground nrgBackground =
                NRGBackground.createFromBackground(backgroundSet, operationGetNRGStack);

        CachedOperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adderOp =
                new WrapOperationWithProgressReporterAsCached<>(
                        pr -> {
                            IAddVideoStatsModule adder = adderOpWithoutNRG.doOperation(pr);

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
            Operation<Cfg, OperationFailedException> op,
            String name,
            NRGBackgroundAdder<?> nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            boolean addAsDefault) {

        VideoStatsModuleCreator module =
                new CfgModuleCreator(
                        delegate.getName(),
                        name,
                        op,
                        nrgBackground.getNRGBackground(),
                        mpg,
                        markDisplaySettings);
        addModule(module, menu, nrgBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addObjectCollection(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            Operation<ObjectCollection, OperationFailedException> op,
            String name,
            NRGBackgroundAdder<?> nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        VideoStatsModuleCreator module =
                new ObjectCollectionModuleCreator(
                        delegate.getName(), name, op, nrgBackground.getNRGBackground(), mpg);
        addModule(module, menu, nrgBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addCfgSubmenu(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            NamedProvider<Cfg> cfgProvider,
            NRGBackgroundAdder<?> nrgBackground,
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
            NRGBackgroundAdder<?> nrgBackground,
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
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> opAdder,
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
