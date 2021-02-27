/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator.energytree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.energytree.overlayparams.CreateParamsFromOverlay;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePair;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.overlay.Overlay;

public class FeatureTreeModel extends DefaultTreeModel implements UpdatableSinglePair {

    private static final long serialVersionUID = -5795973516009041187L;

    private FeatureListWithRegionMap<FeatureInput> featureListWithRegions;
    private FeatureList<FeatureInput> featureList;

    private Logger logger;
    private SharedFeatureMulti sharedFeatures;

    private FeatureCalculatorMulti<FeatureInput> session;

    private boolean first = true;

    public FeatureTreeModel(
            FeatureListWithRegionMap<FeatureInput> featureListWithRegions,
            SharedFeatureMulti sharedFeatures,
            Logger logger) {
        super(null);

        this.featureListWithRegions = featureListWithRegions;

        // We sort both feature lists in alphabetical order
        this.featureListWithRegions.sortAlphaAscend();

        this.featureList = featureListWithRegions.createFeatureList();

        final TreeNode root = new CustomRootNode(logger.errorReporter());
        setRoot(root);

        this.logger = logger;

        this.sharedFeatures = removeFeaturesFromShared(sharedFeatures, featureList);
    }

    @Override
    public void updateSingle(Overlay overlay, EnergyStack energyStack) {

        try {

            // If we have no mark matching the current id
            if (overlay == null || featureListWithRegions.size() == 0) {
                return;
            }

            CreateFeatureInput<FeatureInput> createParams =
                    CreateParamsFromOverlay.addForOverlay(
                            overlay, energyStack, featureListWithRegions);

            updateOrReload(featureList, createParams, energyStack);

        } catch (OperationFailedException e) {
            logger.errorReporter().recordError(FeatureTreeModel.class, e);
        }
    }

    @Override
    public void updatePair(IdentifiablePair<Overlay> pair, EnergyStack energyStack) {

        try {
            // If we have no mark matching the current id
            if (pair == null || featureListWithRegions.size() == 0) {
                return;
            }

            CreateFeatureInput<FeatureInput> createParams =
                    CreateParamsFromOverlay.addForOverlayPair(
                            pair, energyStack, featureListWithRegions);

            updateOrReload(featureList, createParams, energyStack);

        } catch (OperationFailedException e) {
            logger.errorReporter().recordError(FeatureTreeModel.class, e);
        }
    }

    private void updateOrReload(
            FeatureList<FeatureInput> featureList,
            CreateFeatureInput<FeatureInput> createParams,
            EnergyStack energyStack)
            throws OperationFailedException {

        try {
            CustomRootNode root = (CustomRootNode) getRoot();
            // root.setFeatureCalcParams(featureCalcParams);

            FeatureInitialization initialization = new FeatureInitialization(energyStack);
            if (first) {
                // Later on, both the features in featureList and various dependent features will be
                // called through Subsessions
                //   so we rely on these dependent features being initialised through
                // session.start() as the initialization
                //   procedure is recursive
                session = FeatureSession.with(featureList, initialization, sharedFeatures, logger);

                root.replaceFeatureList(featureList, new FeatureInputSource(createParams, session));

                nodeStructureChanged(root);
                reload();
                first = false;

            } else {

                root.replaceCalcParams(new FeatureInputSource(createParams, session));

                nodeChanged(root);
            }

        } catch (InitException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Removes features from shared, which also exist in the FeatureList, as they should not be in
     * both
     */
    private static SharedFeatureMulti removeFeaturesFromShared(
            SharedFeatureMulti sharedFeatures, FeatureList<FeatureInput> featureList) {
        SharedFeatureMulti dup = sharedFeatures.duplicate();
        dup.removeIfExists(featureList);
        return dup;
    }
}
