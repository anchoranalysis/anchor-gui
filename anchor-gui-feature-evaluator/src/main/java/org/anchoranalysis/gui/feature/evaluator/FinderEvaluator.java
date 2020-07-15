package org.anchoranalysis.gui.feature.evaluator;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.BBoxIntersection;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;

/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

class FinderEvaluator {

	private SharedFeatureMulti sharedFeatureList;
	private Logger logger;
		
	public FinderEvaluator(SharedFeatureMulti sharedFeatureList, Logger logger) {
		super();
		this.sharedFeatureList = sharedFeatureList;
		this.logger = logger;
	}
	
	// We take the first valid mark we can find, or NULL if there aren't any
	public static Overlay findOverlayFromCurrentSelection( OverlayCollection overlays ) {
		
		if (overlays.size()>0) {
			return overlays.get(0);
		} else {
			return null;
		}
	}

	public Pair<Overlay> findPairFromCurrentSelection( OverlayCollection overlays, NRGStackWithParams nrgStack ) throws CreateException {
		
		if (doMarkOrObject(overlays)) {
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( overlays );
			return FinderEvaluator.findPairFromCurrentSelectionMark( cfg, nrgStack, sharedFeatureList, logger );
		} else {
			return FinderEvaluator.findPairFromCurrentSelectionObject( overlays );
		}
	}
	
	
	
	// Decides whether to use mark- or object- features based upon whatever there is more of in the selection
	private static boolean doMarkOrObject( OverlayCollection oc ) {
		int cntMark = 0;
		int cntOther = 0;
		for( Overlay ol : oc ) {
			if (ol instanceof OverlayMark) {
				cntMark++;
			} else {
				cntOther++;
			}
		}
		return cntMark > cntOther;
	}

	private static Pair<Overlay> findPairFromCurrentSelectionObject( OverlayCollection oc ) {

		if (oc.size()<=1) {
			return null;
		}
			
		// We always take the first two
		return new Pair<>( oc.get(0), oc.get(1) );
	}
	
	private static Pair<Overlay> findPairFromCurrentSelectionMark(
		Cfg cfg,
		NRGStackWithParams raster,
		SharedFeatureMulti sharedFeatureList,
		Logger logger
	) throws CreateException {
		
		RegionMembershipWithFlags regionMembership = RegionMapSingleton
				.instance()
				.membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		
		if (cfg.size()<=1) {
			return null;
		}
		
		EdgeTester edgeTester = new EdgeTester(
			raster,
			sharedFeatureList,
			logger
		);
		
		// We loop through all permutations of selected Marks, and test if a pair
		//  can be found amongst them
		for (Mark m1 : cfg ) {
			
			assert(m1!=null);
			
			for (Mark m2 : cfg) {
				
				// Let's only do each combination once
				if (m1.getId()>=m2.getId()) {
					continue;
				}
				
				assert(m2!=null);
								
				if (edgeTester.canGenerateEdge( m1, m2 )) {
					return new Pair<>(
						new OverlayMark(m1, regionMembership),
						new OverlayMark(m2, regionMembership)
					);
				}
			}
			
		}
		
		return null;
	}
	
	public static class EdgeTester {

		// WE HARDCODE AN OVERLAP CRITERIA FOR NOW
		private AddCriteriaPair addCriteria = new BBoxIntersection();
		
		// We always use a simple RegionMap
		private RegionMap regionMap = new RegionMap(0);
		
		private NRGStackWithParams raster;
		private Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session;
		
		public EdgeTester(
			NRGStackWithParams raster,
			SharedFeatureMulti sharedFeatureList,
			Logger logger
		) throws CreateException {
			
			this.raster = raster;
			this.session = createSession(sharedFeatureList, logger);
		}
		
		public boolean canGenerateEdge( Mark m1, Mark m2 ) throws CreateException {
			return addCriteria.generateEdge(
				PxlMarkMemoFactory.create( m1, raster.getNrgStack(), regionMap ),
				PxlMarkMemoFactory.create( m2, raster.getNrgStack(), regionMap ),
				raster,
				session,
				raster.getDimensions().getZ()>1
			).isPresent();
		}
		
		private Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> createSession(
			SharedFeatureMulti sharedFeatureList,
			Logger logger	
		) throws CreateException {
			Optional<FeatureList<FeatureInputPairMemo>> relevantFeatures = addCriteria.orderedListOfFeatures();
			if (relevantFeatures.isPresent() && relevantFeatures.get().size()>0) {
				
				try {
					return Optional.of(
						FeatureSession.with(
							relevantFeatures.get(),
							new FeatureInitParams( raster.getParams() ),
							sharedFeatureList,
							logger
						)
					);
				} catch (FeatureCalcException e) {
					throw new CreateException(e);
				}

			} else {
				return Optional.empty();
			}
		}		
	}
}
