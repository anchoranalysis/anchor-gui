/* (C)2020 */
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
            if (finder.doFind(manifests.getFileManifest().doOperation())) {}
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
                operationBwsa.nrgBackground().getNRGBackground().getBackgroundSet(),
                finderContext.getContext());
    }
}
