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

package org.anchoranalysis.gui.feature.evaluator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.mpp.feature.addcriteria.BoundingBoxIntersection;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.PxlMarkMemoFactory;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.mpp.overlay.OverlayMark;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.OverlayCollection;

@AllArgsConstructor
class FinderEvaluator {

    private SharedFeatureMulti sharedFeatureList;
    private Logger logger;

    // We take the first valid mark we can find, or null if there aren't any
    public static Overlay findOverlayFromCurrentSelection(OverlayCollection overlays) {

        if (overlays.size() > 0) {
            return overlays.get(0);
        } else {
            return null;
        }
    }

    public IdentifiablePair<Overlay> findPairFromCurrentSelection(
            OverlayCollection overlays, EnergyStack energyStack) throws CreateException {

        if (doMarkOrObject(overlays)) {
            MarkCollection marks = OverlayCollectionMarkFactory.marksFromOverlays(overlays);
            return FinderEvaluator.findPairFromCurrentSelectionMark(
                    marks, energyStack, sharedFeatureList, logger);
        } else {
            return FinderEvaluator.findPairFromCurrentSelectionObject(overlays);
        }
    }

    // Decides whether to use mark- or object- features based upon whatever there is more of in the
    // selection
    private static boolean doMarkOrObject(OverlayCollection oc) {
        int cntMark = 0;
        int cntOther = 0;
        for (Overlay ol : oc) {
            if (ol instanceof OverlayMark) {
                cntMark++;
            } else {
                cntOther++;
            }
        }
        return cntMark > cntOther;
    }

    private static IdentifiablePair<Overlay> findPairFromCurrentSelectionObject(
            OverlayCollection oc) {

        if (oc.size() <= 1) {
            return null;
        }

        // We always take the first two
        return new IdentifiablePair<>(oc.get(0), oc.get(1));
    }

    private static IdentifiablePair<Overlay> findPairFromCurrentSelectionMark(
            MarkCollection marks,
            EnergyStack raster,
            SharedFeatureMulti sharedFeatureList,
            Logger logger)
            throws CreateException {

        RegionMembershipWithFlags regionMembership =
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

        if (marks.size() <= 1) {
            return null;
        }

        EdgeEvaluator edgeTester = new EdgeEvaluator(raster, sharedFeatureList, logger);

        // We loop through all permutations of selected Marks, and test if a pair
        //  can be found amongst them
        for (Mark m1 : marks) {

            assert (m1 != null);

            for (Mark m2 : marks) {

                // Let's only do each combination once
                if (m1.getIdentifier() >= m2.getIdentifier()) {
                    continue;
                }

                assert (m2 != null);

                if (edgeTester.canGenerateEdge(m1, m2)) {
                    return new IdentifiablePair<>(
                            new OverlayMark(m1, regionMembership),
                            new OverlayMark(m2, regionMembership));
                }
            }
        }

        return null;
    }

    public static class EdgeEvaluator {

        // WE HARDCODE AN OVERLAP CRITERIA FOR NOW
        private AddCriteriaPair addCriteria = new BoundingBoxIntersection();

        // We always use a simple RegionMap
        private RegionMap regionMap = new RegionMap(0);

        private EnergyStack raster;
        private Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session;

        public EdgeEvaluator(EnergyStack raster, SharedFeatureMulti sharedFeatureList, Logger logger)
                throws CreateException {

            this.raster = raster;
            this.session = createSession(sharedFeatureList, logger);
        }

        public boolean canGenerateEdge(Mark m1, Mark m2) throws CreateException {
            return addCriteria
                    .generateEdge(
                            PxlMarkMemoFactory.create(m1, raster.withoutParams(), regionMap),
                            PxlMarkMemoFactory.create(m2, raster.withoutParams(), regionMap),
                            raster,
                            session,
                            raster.dimensions().z() > 1)
                    .isPresent();
        }

        private Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> createSession(
                SharedFeatureMulti sharedFeatureList, Logger logger) throws CreateException {
            Optional<FeatureList<FeatureInputPairMemo>> relevantFeatures =
                    addCriteria.orderedListOfFeatures();
            if (relevantFeatures.isPresent() && relevantFeatures.get().size() > 0) {

                try {
                    return Optional.of(
                            FeatureSession.with(
                                    relevantFeatures.get(),
                                    new FeatureInitParams(raster.getParams()),
                                    sharedFeatureList,
                                    logger));
                } catch (InitException e) {
                    throw new CreateException(e);
                }

            } else {
                return Optional.empty();
            }
        }
    }
}
