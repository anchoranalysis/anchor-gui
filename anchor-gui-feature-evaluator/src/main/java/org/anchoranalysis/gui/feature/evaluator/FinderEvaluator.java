package org.anchoranalysis.gui.feature.evaluator;

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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemoFactory;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;
import ch.ethz.biol.cell.mpp.nrg.feature.session.FeatureSessionCreateParamsMPP;
import ch.ethz.biol.cell.mpp.pair.Pair;
import ch.ethz.biol.cell.mpp.pair.addcriteria.AddCriteriaPair;
import ch.ethz.biol.cell.mpp.pair.addcriteria.BBoxIntersection;
import overlay.OverlayCollectionMarkFactory;
import overlay.OverlayMark;

class FinderEvaluator {

	private SharedFeatureSet sharedFeatureList;
	private LogErrorReporter logger;
		
	public FinderEvaluator(SharedFeatureSet sharedFeatureList, LogErrorReporter logger) {
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
		
		if (doMarkOrObjMask(overlays)) {
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( overlays );
			return FinderEvaluator.findPairFromCurrentSelectionMark( cfg, nrgStack, sharedFeatureList, logger );
		} else {
			return FinderEvaluator.findPairFromCurrentSelectionObjMask( overlays );
		}
	}
	
	
	
	// Decides whether to use Mark features or ObjMask Features based upon whatever there is more of in the selection
	private static boolean doMarkOrObjMask( OverlayCollection oc ) {
		int cntMark = 0;;
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

	private static Pair<Overlay> findPairFromCurrentSelectionObjMask( OverlayCollection oc ) throws CreateException {

		if (oc.size()<=1) {
			return null;
		}
			
		// We always take the first two
		return new Pair<Overlay>( oc.get(0), oc.get(1) );
	}
	
	private static Pair<Overlay> findPairFromCurrentSelectionMark(
		Cfg cfg,
		NRGStackWithParams raster,
		SharedFeatureSet sharedFeatureList,
		LogErrorReporter logger
	) throws CreateException {
		
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
					return new Pair<Overlay>( new OverlayMark(m1), new OverlayMark(m2) );
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
		private FeatureSessionCreateParamsMPP session;
		
		public EdgeTester(
			NRGStackWithParams raster,
			SharedFeatureSet sharedFeatureList,
			LogErrorReporter logger
		) throws CreateException {
			
			this.raster = raster;
			
			FeatureList relevantFeatures = addCriteria.orderedListOfFeatures();
			if (relevantFeatures.size()>0) {
				session = new FeatureSessionCreateParamsMPP( relevantFeatures, raster.getNrgStack(), raster.getParams() );
				try {
					session.start( new FeatureInitParams(null), sharedFeatureList, logger);
				} catch (InitException e) {
					throw new CreateException(e);
				}
			} else {
				session = null;
			}
		}
		
		public boolean canGenerateEdge( Mark m1, Mark m2 ) throws CreateException {
			PxlMarkMemo pmm1 = PxlMarkMemoFactory.create( m1, raster.getNrgStack(), regionMap );
			PxlMarkMemo pmm2 = PxlMarkMemoFactory.create( m2, raster.getNrgStack(), regionMap );
			return (addCriteria.generateEdge(pmm1, pmm2, raster, session, raster.getDimensions().getZ()>1 )!=null);
		}
	}

	
}
