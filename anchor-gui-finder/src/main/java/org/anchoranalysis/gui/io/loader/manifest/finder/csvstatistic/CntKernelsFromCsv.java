package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

/*-
 * #%L
 * anchor-gui-finder
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

class CntKernelsFromCsv {

	/**
	 * Counts the number of kernels contained in a csvStatsAgg.csv file
	 * 
	 * Assumes there is an equal number of Prop and Accpt columns for kernels
	 * Assumes columns are in ascending contiguous order
	 * 
	 * @param headers
	 * @return the total number of kernels
	 */
	public static int apply( String[] headers ) {
		// Find the last header that matches Prop
		
		String finalProp = lastPropStr(headers);
		if (finalProp!=null) {
			String numPart = extractNumPart(finalProp);
			return Integer.parseInt(numPart) + 1;
		} else {
			return 0;
		}
	}
	
	private static String lastPropStr( String[] headers ) {
		String finalProp = null;
		for( String s : headers ) {
			if (s.startsWith("Prop")) {
				finalProp = s;
			}
		}
		return finalProp;
	}
	
	private static String extractNumPart( String s ) {
		return s.substring(4);
	}
	
}
