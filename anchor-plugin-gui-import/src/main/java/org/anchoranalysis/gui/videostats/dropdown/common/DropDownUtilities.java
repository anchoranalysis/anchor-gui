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
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModuleSupplier;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.OperationEnergyStackFromMarkEvaluatorSet;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.MarksModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.ObjectCollectionModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.sequencetype.StringsWithoutOrder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.mark.MarkCollection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DropDownUtilities {

    public static void addAllProposerEvaluator(
            BoundVideoStatsModuleDropDown dropDown,
            AddVideoStatsModuleSupplier adderOpWithoutEnergy,
            BackgroundSetProgressingSupplier backgroundSet,
            MarkEvaluatorSetForImage markEvaluatorSet,
            OutputWriteSettings outputWriteSettings,
            boolean addEnergyAdder,
            VideoStatsModuleGlobalParams mpg) {
        // Generating a EnergyStackWithParams from the markEvaluatorSet
        OperationEnergyStackFromMarkEvaluatorSet operationgetEnergyStack =
                new OperationEnergyStackFromMarkEvaluatorSet(markEvaluatorSet);

        EnergyBackground energyBackground =
                EnergyBackground.createFromBackground(
                        backgroundSet, () -> operationgetEnergyStack.get(ProgressIgnore.get()));

        AddVideoStatsModuleSupplier adderOp =
                AddVideoStatsModuleSupplier.cache(
                        progress -> {
                            AddVideoStatsModule adder = adderOpWithoutEnergy.get(progress);

                            if (addEnergyAdder) {
                                energyBackground.addEnergyStackToAdder(adder);
                            }
                            return adder;
                        });

        VideoStatsModuleCreator moduleCreator =
                new ProposerEvaluatorModuleCreator(
                        markEvaluatorSet,
                        energyBackground,
                        outputWriteSettings,
                        operationgetEnergyStack,
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

    public static void addMarks(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            OverlayCollectionSupplier<MarkCollection> op,
            String name,
            EnergyBackgroundAdder energyBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            boolean addAsDefault) {

        VideoStatsModuleCreator module =
                new MarksModuleCreator(
                        delegate.getName(),
                        name,
                        op,
                        energyBackground.getBackground(),
                        mpg,
                        markDisplaySettings);
        addModule(module, menu, energyBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addObjectCollection(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            OverlayCollectionSupplier<ObjectCollection> op,
            String name,
            EnergyBackgroundAdder energyBackground,
            VideoStatsModuleGlobalParams mpg,
            boolean addAsDefault) {
        VideoStatsModuleCreator module =
                new ObjectCollectionModuleCreator(
                        delegate.getName(), name, op, energyBackground.getBackground(), mpg);
        addModule(module, menu, energyBackground.getAdder(), name, mpg, addAsDefault);
    }

    public static void addMarksSubmenu(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            NamedProvider<MarkCollection> marks,
            EnergyBackgroundAdder energyBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            boolean addAsDefault) {
        if (marks.keys().isEmpty()) {
            return;
        }

        VideoStatsOperationMenu subMenu = menu.createSubMenu("Marks", true);

        for (String providerName : marks.keys()) {

            addMarks(
                    subMenu,
                    delegate.createChild(providerName),
                    () -> getFromProvider(marks, providerName),
                    providerName,
                    energyBackground,
                    mpg,
                    markDisplaySettings,
                    addAsDefault);
        }
    }

    public static void addObjectsSubmenu(
            VideoStatsOperationMenu menu,
            BoundVideoStatsModuleDropDown delegate,
            final NamedProvider<ObjectCollection> provider,
            EnergyBackgroundAdder energyBackground,
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
                    energyBackground,
                    mpg,
                    addAsDefault);
        }
    }

    public static InputOutputContext createSubdirectoryContext(
            InputOutputContext inputOutputContext, String subdirectoryName) throws InitException {

        ManifestDirectoryDescription manifestDescription =
                new ManifestDirectoryDescription(
                        "interactiveOutput",
                        "manifestInteractiveOutput",
                        new StringsWithoutOrder());

        // NB: As bindAsSubdirectory can now return nulls, maybe some knock-on bugs are introduced
        // here
        return inputOutputContext.subdirectory(subdirectoryName, manifestDescription, false);
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
