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

package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.overlayparams.CreateParamsFromOverlay;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;

public class FeatureCalcDescriptionTreeModel extends DefaultTreeModel
        implements IUpdatableSinglePair {

    private static final long serialVersionUID = -5795973516009041187L;

    private FeatureListWithRegionMap<FeatureInput> featureListWithRegions;
    private FeatureList<FeatureInput> featureList;

    private Logger logger;
    private SharedFeatureMulti sharedFeatures;

    private FeatureCalculatorMulti<FeatureInput> session = null;

    // private static Log log = LogFactory.getLog(FeatureCalcDescriptionTreeModel.class);

    private boolean first = true;

    public FeatureCalcDescriptionTreeModel(
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
    public void updateSingle(Overlay overlay, NRGStackWithParams nrgStack) {

        try {

            // If we have no mark matching the current id
            if (overlay == null || featureListWithRegions.size() == 0) {
                return;
            }

            CreateFeatureInput<FeatureInput> createParams =
                    CreateParamsFromOverlay.addForOverlay(
                            overlay, nrgStack, featureListWithRegions);

            updateOrReload(featureList, createParams, nrgStack);

        } catch (OperationFailedException e) {
            logger.errorReporter().recordError(FeatureCalcDescriptionTreeModel.class, e);
        }
    }

    @Override
    public void updatePair(Pair<Overlay> pair, NRGStackWithParams nrgStack) {

        try {
            // If we have no mark matching the current id
            if (pair == null || featureListWithRegions.size() == 0) {
                return;
            }

            CreateFeatureInput<FeatureInput> createParams =
                    CreateParamsFromOverlay.addForOverlayPair(
                            pair, nrgStack, featureListWithRegions);

            updateOrReload(featureList, createParams, nrgStack);

        } catch (OperationFailedException e) {
            logger.errorReporter().recordError(FeatureCalcDescriptionTreeModel.class, e);
        }
    }

    private void updateOrReload(
            FeatureList<FeatureInput> featureList,
            CreateFeatureInput<FeatureInput> createParams,
            NRGStackWithParams nrgStack)
            throws OperationFailedException {

        try {
            CustomRootNode root = (CustomRootNode) getRoot();
            // root.setFeatureCalcParams(featureCalcParams);

            FeatureInitParams paramsInit = new FeatureInitParams(nrgStack);
            if (first) {
                // Later on, both the features in featureList and various dependent features will be
                // called through Subsessions
                //   so we rely on these dependent features being initialised through
                // session.start() as the initialization
                //   procedure is recursive
                session = FeatureSession.with(featureList, paramsInit, sharedFeatures, logger);

                root.replaceFeatureList(featureList, new ParamsSource(createParams, session));

                nodeStructureChanged(root);
                reload();
                first = false;

            } else {

                root.replaceCalcParams(new ParamsSource(createParams, session));

                nodeChanged(root);
            }

        } catch (FeatureCalcException e) {
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
