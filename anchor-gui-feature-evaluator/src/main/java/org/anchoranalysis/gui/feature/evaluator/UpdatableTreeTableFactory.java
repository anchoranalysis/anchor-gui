/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.FeatureCalcDescriptionTreeRenderData;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.FeatureCalcDescriptionTreeRowModel;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.feature.evaluator.treetable.ITreeTableModel;
import org.anchoranalysis.gui.feature.evaluator.treetable.TreeTableProperties;
import org.anchoranalysis.gui.feature.evaluator.treetable.TreeTableWithModelMultiplex;

class UpdatableTreeTableFactory {

    public static UpdatableTreeTable create(
            OverlayDescriptionPanel overlayDescriptionPanel,
            FeatureListSrc featureListSrc,
            Logger logger) {

        ITreeTableModel treeTable = createTreeTable(featureListSrc, logger);

        SinglePairUpdater markPairList =
                new SinglePairUpdater(
                        overlayDescriptionPanel,
                        new FinderEvaluator(featureListSrc.sharedFeatures(), logger),
                        treeTable);

        return new UpdatableTreeTable(treeTable, markPairList);
    }

    private static ITreeTableModel createTreeTable(FeatureListSrc featureListSrc, Logger logger) {
        FeatureCalcDescriptionTreeRowModel rowModel = new FeatureCalcDescriptionTreeRowModel();

        TreeTableProperties properties =
                new TreeTableProperties(
                        rowModel,
                        new FeatureCalcDescriptionTreeRenderData(logger.errorReporter()),
                        "NRG Term",
                        logger);

        ITreeTableModel treeTable = new TreeTableWithModelMultiplex(properties, featureListSrc);
        treeTable.addMouseListenerToOutline(
                new ShowErrorMessageMouseListener(treeTable, logger.errorReporter()));
        return treeTable;
    }
}
