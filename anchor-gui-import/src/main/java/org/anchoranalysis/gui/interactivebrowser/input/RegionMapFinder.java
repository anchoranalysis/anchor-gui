package org.anchoranalysis.gui.interactivebrowser.input;

/*-
 * #%L
 * anchor-gui-import
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
import org.anchoranalysis.feature.bean.list.FeatureList;

import ch.ethz.biol.cell.mpp.nrg.NamedNRGSchemeSet;
import ch.ethz.biol.cell.mpp.nrg.nrgscheme.NRGScheme;

class RegionMapFinder {
		
	public static void addFromNrgScheme( NamedNRGSchemeSet nrgElemSet, NRGScheme nrgScheme ) throws CreateException {
			
		nrgElemSet.add(
			"elem_ind",
			new NRGScheme( new FeatureList( nrgScheme.getElemInd()	),
			new FeatureList( nrgScheme.getElemPair() ),
			nrgScheme.getRegionMap()
		));
		nrgElemSet.add(
			"elem_pair",
			new NRGScheme( new FeatureList(nrgScheme.getElemInd() ),
			new FeatureList( nrgScheme.getElemPair() ),
			nrgScheme.getRegionMap()
		));
	}
}
