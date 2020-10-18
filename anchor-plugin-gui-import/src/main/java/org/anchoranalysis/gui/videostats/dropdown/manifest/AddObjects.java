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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.gui.finder.FinderEnergyStack;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderMarksFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderObjectsDirectory;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.EnergyTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.feature.energy.marks.MarksWithEnergyBreakdown;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

@AllArgsConstructor
class AddObjects {

    private BoundVideoStatsModuleDropDown delegate;
    private CoupledManifests manifests;
    private FinderEnergyStack finderEnergyStack;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    public boolean apply(OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy) {

        VideoStatsOperationMenu subMenu = delegate.getRootMenu().createSubMenu("Objects", true);

        boolean defaultAdded = false;

        if (fromObjectCollectionFolder(subMenu, operationBwsaWithEnergy)) {
            defaultAdded = true;
        }

        if (fromSerializedMarks(operationBwsaWithEnergy)) {
            defaultAdded = true;
        }

        fromMarks(operationBwsaWithEnergy);

        return defaultAdded;
    }

    private boolean fromObjectCollectionFolder(
            VideoStatsOperationMenu subMenu,
            OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy) {
        try {
            final FinderObjectsDirectory finderObjects =
                    new FinderObjectsDirectory(OutputterDirectories.OBJECTS);
            finderObjects.doFind(manifests.getJobManifest().get());

            if (finderObjects.exists()) {

                NamedProvider<ObjectCollection> providers =
                        finderObjects.createNamedProvider(mpg.getLogger());

                for (String key : providers.keys()) {
                    DropDownUtilities.addObjectCollection(
                            subMenu,
                            delegate,
                            OverlayCollectionSupplier.cache(
                                    () -> {
                                        try {
                                            return providers.getException(key);
                                        } catch (NamedProviderGetException e) {
                                            throw new OperationFailedException(e);
                                        }
                                    }),
                            key,
                            operationBwsaWithEnergy.energyBackground(),
                            mpg,
                            true);
                }
                return true;
            }

        } catch (OperationFailedException e) {
            mpg.getLogger().errorReporter().recordError(ManifestDropDown.class, e);
        }

        return false;
    }
    
    private boolean fromSerializedMarks(
            OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy) {
        try {
            final FinderSerializedObject<MarksWithEnergyBreakdown> finderFinalMarks =
                    new FinderSerializedObject<>("marks", mpg.getLogger().errorReporter());
            finderFinalMarks.doFind(manifests.getJobManifest().get());

            if (finderFinalMarks.exists()) {

                CachedSupplier<BoundedIndexContainer<IndexableMarksWithEnergy>, OperationFailedException>
                        op = CachedSupplier.cache(
                           () -> createSerializedMarksContainer(finderFinalMarks)
                        );

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
    
    private BoundedIndexContainer<IndexableMarksWithEnergy> createSerializedMarksContainer(FinderSerializedObject<MarksWithEnergyBreakdown> finderFinalMarks) throws OperationFailedException {
        try {
            IndexableMarksWithEnergy instantState = new IndexableMarksWithEnergy(
                0, finderFinalMarks.get());
            return new SingleContainer<>(instantState, 0, false);
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    private void fromMarks(OperationCreateBackgroundSetWithAdder operationBwsaWithEnergy) {

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
