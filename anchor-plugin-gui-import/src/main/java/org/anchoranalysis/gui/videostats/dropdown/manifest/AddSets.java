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
import org.anchoranalysis.gui.finder.FinderEnergyStack;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderMarksWithEnergy;
import org.anchoranalysis.gui.io.loader.manifest.finder.MarksWithEnergyFinderContext;
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
            MarksWithEnergyFinderContext context) {
        super();
        this.dropDown = dropDown;
        this.operationBwsa = operationBwsa;
        this.finderContext =
                new FinderContext(
                        operationBwsa.energyBackground(),
                        dropDown,
                        dropDown.getRootMenu(),
                        context);
    }

    public void apply(CoupledManifests manifests, FinderEnergyStack finderEnergyStack)
            throws OperationFailedException {

        // INDIVIDUAL SETS
        FinderMarksWithEnergy finderProposedSecondary =
                createFinder(
                        "Proposed (Secondary)",
                        "csvStatsAgg",
                        "marksProposalSecondary",
                        null,
                        null);
        FinderMarksWithEnergy finderProposed =
                createFinder(
                        "Proposed", "csvStatsAgg", "marksProposal", finderProposedSecondary, null);

        FinderMarksWithEnergy finderSelected =
                createFinder(
                        "Selected",
                        "csvStatsAgg",
                        "marks",
                        finderProposed,
                        finderProposedSecondary);
        FinderMarksWithEnergy finderBest =
                createFinder(
                        "Best",
                        "csvStatsBest",
                        "marksSerializedBest",
                        finderProposed,
                        finderProposedSecondary);

        addIndividualSets(
                finderSelected, finderBest, finderProposed, finderProposedSecondary, manifests);

        dropDown.getRootMenu().addSeparator();

        // Now we add the combined

        // COMBINED SETS
        addCombinedSets(finderSelected, finderBest, finderProposed);
    }

    private FinderMarksWithEnergy createFinder(
            String setName,
            String csvStatsName,
            String manifestNameMarksHistory,
            FinderMarksWithEnergy secondary,
            FinderMarksWithEnergy tertiary) {
        return new FinderMarksWithEnergy(
                setName,
                csvStatsName,
                manifestNameMarksHistory,
                finderContext,
                secondary,
                tertiary);
    }

    private void addIndividualSets(
            FinderMarksWithEnergy finderSelected,
            FinderMarksWithEnergy finderBest,
            FinderMarksWithEnergy finderProposed,
            FinderMarksWithEnergy finderProposedSecondary,
            CoupledManifests manifests)
            throws OperationFailedException {

        FinderMarksWithEnergy[] allFinderMarks =
                new FinderMarksWithEnergy[] {
                    finderSelected, finderBest, finderProposed, finderProposedSecondary
                };

        for (FinderMarksWithEnergy finder : allFinderMarks) {
            if (finder.doFind(manifests.getFileManifest().get())) {
                // NOTHING TO DO
            }
        }
    }

    private void addCombinedSets(
            FinderMarksWithEnergy finderSelected,
            FinderMarksWithEnergy finderBest,
            FinderMarksWithEnergy finderProposed)
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
            FinderMarksWithEnergy finderFirst,
            FinderMarksWithEnergy finderSecond,
            CombinedMenu combinedDropDown)
            throws MenuAddException {
        combinedDropDown.addCombination(
                finderFirst,
                finderSecond,
                operationBwsa.energyBackground().getBackground().getBackgroundSet(),
                finderContext.getContext());
    }
}
