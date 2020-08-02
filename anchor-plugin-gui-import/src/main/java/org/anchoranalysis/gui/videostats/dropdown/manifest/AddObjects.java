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
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.core.cache.CacheCall;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderObjectCollectionFolder;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.NRGTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.mpp.sgmn.define.OutputterDirectories;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

@AllArgsConstructor
class AddObjects {

    private BoundVideoStatsModuleDropDown delegate;
    private CoupledManifests manifests;
    private FinderNrgStack finderNrgStack;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    public boolean apply(OperationCreateBackgroundSetWithAdder operationBwsaWithNRG) {

        VideoStatsOperationMenu subMenu = delegate.getRootMenu().createSubMenu("Objects", true);

        boolean defaultAdded = false;

        if (fromObjectCollectionFolder(subMenu, operationBwsaWithNRG)) {
            defaultAdded = true;
        }

        if (fromSerializedCfgNRG(operationBwsaWithNRG)) {
            defaultAdded = true;
        }

        fromCfg(operationBwsaWithNRG);

        return defaultAdded;
    }

    private boolean fromObjectCollectionFolder(
            VideoStatsOperationMenu subMenu,
            OperationCreateBackgroundSetWithAdder operationBwsaWithNRG) {
        try {
            final FinderObjectCollectionFolder finderObjects =
                    new FinderObjectCollectionFolder(OutputterDirectories.OBJECT);
            finderObjects.doFind(manifests.getFileManifest().call());

            if (finderObjects.exists()) {

                NamedProvider<ObjectCollection> providers =
                        finderObjects.createNamedProvider(mpg.getLogger());

                for (String key : providers.keys()) {
                    DropDownUtilities.addObjectCollection(
                            subMenu,
                            delegate,
                            CacheCall.of( () -> {
                                try {
                                    return providers.getException(key);
                                } catch (NamedProviderGetException e) {
                                    throw new OperationFailedException(e);
                                }      
                            }),
                            key,
                            operationBwsaWithNRG.nrgBackground(),
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

    private boolean fromSerializedCfgNRG(
            OperationCreateBackgroundSetWithAdder operationBwsaWithNRG) {
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

    private void fromCfg(OperationCreateBackgroundSetWithAdder operationBwsaWithNRG) {

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
}
