package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlap.OverlapUtilities;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.cfg.Cfg;


// Always uses the first region
class OverlapChecker {

	// START PARAMETERS for overlap check
	private double largeOverlapThreshold = 0.3;
	// END PARAMETERS
	
	private NRGStack nrgStack;
	private ToolErrorReporter errorReporter;
	private RegionMap regionMap;
	
	public OverlapChecker(ImageDim dim, RegionMap regionMap, ToolErrorReporter errorReporter) {
		super();
		this.nrgStack = new NRGStack(dim);
		this.errorReporter = errorReporter;
		this.regionMap = regionMap;
	}
	
	private boolean hasLargeOverlap( PxlMarkMemo pmProp1, PxlMarkMemo pmProp2 ) throws OperationFailedException {
		
		try {
			double overlap = OverlapUtilities.overlapWith(pmProp1,pmProp2,0);
			double overlapRatio = calcOverlapRatio(pmProp1, pmProp2, overlap, 0);
			return (overlapRatio>largeOverlapThreshold);
		} catch (FeatureCalcException | ExecuteException e) {
			throw new OperationFailedException(e);
		}
		
	}
	
	// We look for larger overlap to warn the user
	public boolean hasLargeOverlap( Cfg proposed, Cfg existing ) {
		
		for( Mark prop : proposed ) {
			
			PxlMarkMemo pmProp = new PxlMarkMemo(prop,nrgStack,regionMap,null);
			
			for( Mark exst : existing ) {
				PxlMarkMemo pmExst = new PxlMarkMemo(exst,nrgStack,regionMap,null);
				
				try {
					if( pmProp.doOperation().getBoundingBox(0).hasIntersection(pmExst.doOperation().getBoundingBox(0))) {
						if (hasLargeOverlap(pmProp,pmExst)) {
							return true;
						}
					}
				} catch( OperationFailedException | ExecuteException e) {
					errorReporter.showError(OverlapChecker.class, "Cannot calculate overlap", e.toString() );
				}
			}
		}
		
		return false;
	}
	
	
	private static double calcMinVolume( PxlMarkMemo obj1, PxlMarkMemo obj2, int regionID ) throws FeatureCalcException {
		double size1 =  obj1.getMark().volume(0);
		double size2 =  obj2.getMark().volume(0);
		return Math.min( size1, size2 );
	}
	
	public static double calcOverlapRatio( PxlMarkMemo obj1, PxlMarkMemo obj2, double overlap, int regionID ) throws FeatureCalcException {
		double volume = calcMinVolume( obj1, obj2, regionID );
		return overlap / volume;
	}
}
