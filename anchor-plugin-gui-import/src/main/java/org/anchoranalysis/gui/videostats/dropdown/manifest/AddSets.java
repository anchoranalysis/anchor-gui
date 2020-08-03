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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.gui.io.loader.manifest.finder.CfgNRGFinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgNRGSet;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderContext;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.CombinedMenu;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

class AddSets {

    private BoundVideoStatsModuleDropDown dropDown;

    private OperationCreateBackgroundSetWithAdder operationBwsa;

    private FinderContext finderContext;

    public AddSets(
            BoundVideoStatsModuleDropDown dropDown,
            OperationCreateBackgroundSetWithAdder operationBwsa,
            CfgNRGFinderContext context) {
        super();
        this.dropDown = dropDown;
        this.operationBwsa = operationBwsa;
        this.finderContext =
                new FinderContext(
                        operationBwsa.nrgBackground(), dropDown, dropDown.getRootMenu(), context);
    }

    public void apply(CoupledManifests manifests, FinderNrgStack finderNrgStack)
            throws OperationFailedException {

        // INDIVIDUAL SETS
        FinderCfgNRGSet finderProposedSecondary =
                createFinder(
                        "Proposed (Secondary)",
                        "csvStatsAgg",
                        "cfgNRGProposalSecondary",
                        null,
                        null);
        FinderCfgNRGSet finderProposed =
                createFinder(
                        "Proposed", "csvStatsAgg", "cfgNRGProposal", finderProposedSecondary, null);

        FinderCfgNRGSet finderSelected =
                createFinder(
                        "Selected",
                        "csvStatsAgg",
                        "cfgNRG",
                        finderProposed,
                        finderProposedSecondary);
        FinderCfgNRGSet finderBest =
                createFinder(
                        "Best",
                        "csvStatsBest",
                        "cfgNRGSerializedBest",
                        finderProposed,
                        finderProposedSecondary);

        addIndividualSets(
                finderSelected, finderBest, finderProposed, finderProposedSecondary, manifests);

        dropDown.getRootMenu().addSeparator();

        // Now we add the combined

        // COMBINED SETS
        addCombinedSets(finderSelected, finderBest, finderProposed);
    }

    private FinderCfgNRGSet createFinder(
            String setName,
            String csvStatsName,
            String manifestNameCfgNRGHistory,
            FinderCfgNRGSet secondary,
            FinderCfgNRGSet tertiary) {
        return new FinderCfgNRGSet(
                setName,
                csvStatsName,
                manifestNameCfgNRGHistory,
                finderContext,
                secondary,
                tertiary);
    }

    private void addIndividualSets(
            FinderCfgNRGSet finderSelected,
            FinderCfgNRGSet finderBest,
            FinderCfgNRGSet finderProposed,
            FinderCfgNRGSet finderProposedSecondary,
            CoupledManifests manifests)
            throws OperationFailedException {

        FinderCfgNRGSet[] allFinderCfgNRG =
                new FinderCfgNRGSet[] {
                    finderSelected, finderBest, finderProposed, finderProposedSecondary
                };

        for (FinderCfgNRGSet finder : allFinderCfgNRG) {
            if (finder.doFind(manifests.getFileManifest().get())) {}
        }
    }

    private void addCombinedSets(
            FinderCfgNRGSet finderSelected,
            FinderCfgNRGSet finderBest,
            FinderCfgNRGSet finderProposed)
            throws OperationFailedException {
        CombinedMenu combinedDropDown =
                new CombinedMenu(
                        dropDown.getRootMenu(),
                        dropDown.createAddModuleToMenu(operationBwsa.operationAdder()));

        try {
            addCombined(finderSelected, finderProposed, combinedDropDown);
            addCombined(finderSelected, finderBest, combinedDropDown);
            addCombined(finderBest, finderProposed, combinedDropDown);
        } catch (MenuAddException e) {
            throw new OperationFailedException(e);
        }
    }

    private void addCombined(
            FinderCfgNRGSet finderFirst,
            FinderCfgNRGSet finderSecond,
            CombinedMenu combinedDropDown)
            throws MenuAddException {
        combinedDropDown.addCombination(
                finderFirst,
                finderSecond,
                operationBwsa.nrgBackground().getBackground().getBackgroundSet(),
                finderContext.getContext());
    }
}
