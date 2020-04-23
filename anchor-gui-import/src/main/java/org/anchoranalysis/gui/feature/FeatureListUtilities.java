package org.anchoranalysis.gui.feature;

import java.util.function.Function;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;


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
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.operator.Sum;
import org.anchoranalysis.feature.calc.params.FeatureInput;

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
	public static <T extends FeatureInput> FeatureListWithRegionMap<T> createFeatureList(
		NamedNRGSchemeSet elemSet,
		Function<NRGScheme,FeatureList<T>> funcExtractList,
		boolean includeLastExecution
	) {
		
		FeatureListWithRegionMap<T> featureList = new FeatureListWithRegionMap<>(); 
		
		for( NamedBean<NRGScheme> nnec : elemSet ) {
			
			NRGScheme nrgScheme = nnec.getValue();
			
			RegionMap regionMap = nrgScheme.getRegionMap();
			
			FeatureList<T> extractedList = funcExtractList.apply(nrgScheme);
			
			// This is a bit hacky, but we convert beginning with lastExecution to be a separate collection
			//  but anything else we treat as a top-level item
			if (nnec.getName().startsWith("lastExecution")) {
				
				if (includeLastExecution) {
					featureList.add(
						sumFeatures(extractedList, nnec.getName()),
						regionMap
					);
				}
				
			} else if (nnec.getName().equals("user_defined")) {
				
				for(Feature<T> f : extractedList ) {
					featureList.add(f, regionMap );
				}
				
			} else if (extractedList.size()>0) {

				featureList.add(
					sumFeatures(extractedList, nnec.getName()),
					regionMap
				);
			
			}
		}
		
		return featureList;
	}
	
	private static <T extends FeatureInput> Feature<T> sumFeatures( FeatureList<T> extractedList,	String name ) {
		Sum<T> rootFeature = new Sum<>();
		rootFeature.setList( extractedList );
		rootFeature.setCustomName(name);
		return rootFeature;
	}
}
