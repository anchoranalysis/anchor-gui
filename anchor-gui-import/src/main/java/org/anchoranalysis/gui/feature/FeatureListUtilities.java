package org.anchoranalysis.gui.feature;

import java.util.List;

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

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.operator.Sum;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.gui.feature.evaluator.params.FeatureCalcParamsFactorySession;
import org.anchoranalysis.gui.serializedobjectset.MarkWithRaster;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;
import ch.ethz.biol.cell.mpp.nrg.NamedNRGSchemeSet;
import ch.ethz.biol.cell.mpp.nrg.nrgscheme.NRGScheme;

public class FeatureListUtilities {

	// Creates a feature list for showing Individual Elems
	
	/**
	 * Creates a FeatureList for a NamedNRGSchemeSet by extracting a particular clique-size
	 * 
	 * @param elemSet  
	 * @param cliqueSize 1 for pairwise, 0 for unary, -1 for all
	 * @param includeLastExecution
	 * @return
	 */
	public static FeatureListWithRegionMap createFeatureList( NamedNRGSchemeSet elemSet, int cliqueSize, boolean includeLastExecution ) {
		
		FeatureListWithRegionMap featureList = new FeatureListWithRegionMap(); 
		
		for( NamedBean<NRGScheme> nnec : elemSet ) {
			
			RegionMap regionMap = nnec.getValue().getRegionMap();
			
			// This is a bit hacky, but we convert beginning with lastExecution to be a separate collection
			//  but anything else we treat as a top-level item
			if (nnec.getName().startsWith("lastExecution")) {
				
				if (includeLastExecution) {
					Sum rootFeature = new Sum();
					rootFeature.setList( nnec.getValue().getElemByCliqueSize( cliqueSize) );
					rootFeature.setCustomName(nnec.getName());
					featureList.add( rootFeature, regionMap );
				}
			} else if (nnec.getName().equals("user_defined")) {
				
				for(Feature f : nnec.getValue().getElemByCliqueSize(cliqueSize) ) {
					featureList.add(f, regionMap );
				}
				
			} else {
				
				FeatureList fl = nnec.getValue().getElemByCliqueSize(cliqueSize);
				
				if (fl.size()>0) {
					Sum rootFeature = new Sum();
					rootFeature.setList( fl );
					rootFeature.setCustomName(nnec.getName());
					featureList.add(rootFeature, regionMap );
				}
			}
		}
		
		return featureList;
	}


	// listMarks is an optional variable for writing out return values to the list
	public static CalculatedFeatureValues calculateFeatureList(
		FeatureListWithRegionMap featureList,
		IBoundedIndexContainer<MarkWithRaster> cntr,
		List<Mark> listMarks,
		LogErrorReporter logger
	) throws OperationFailedException {
		
		FeatureCalcParamsFactorySession session = new FeatureCalcParamsFactorySession(featureList,logger);
		session.start();
		
		try {
			int numMarks = cntr.getMaximumIndex() - cntr.getMinimumIndex() + 1;
			
			CalculatedFeatureValues featureValues = new CalculatedFeatureValues( numMarks, featureList.size() );
			
			// For each mark
			for (int i = 0; i<numMarks; i++ ) { 
				
				MarkWithRaster markWithRaster = cntr.get( cntr.getMinimumIndex() + i );
				
				Mark mark = markWithRaster.getMark();
				assert(mark!=null);
				
				if (listMarks!=null) {
					listMarks.add(mark);
				}
				
				ResultsVector rv = session.calc( mark, markWithRaster.getNRGStack() );
					
				featureValues.set(i, rv);
			}
			return featureValues;
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}
	
}
