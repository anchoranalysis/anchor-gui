package org.anchoranalysis.gui.io.loader.manifest.finder.probmap;

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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.Finder;

// TODO CURRENTLY UNUSED AS WE DON'T WRITE HTE PROB MAPs to the file system
class FinderProbMapCollection extends Finder implements Iterable<FinderProbMap> {

	// A named collection of ProbMaps
	private Collection<FinderProbMap> map = new ArrayList<>();
	
	private RasterReader rasterReader;
	private ErrorReporter errorReporter;
	
	public FinderProbMapCollection( RasterReader rasterReader, ErrorReporter errorReporter ) {
		this.rasterReader = rasterReader;
		this.errorReporter = errorReporter;
	}
	
	public void add( String probMapOutputName ) {
		
		FinderProbMap finder = new FinderProbMap( rasterReader, probMapOutputName, errorReporter );
		map.add( finder );
	}
	
	// Puts the associated probmaps in the background set, using their displaynames as names
	public void addToBackgroundSet( BackgroundSet bs ) {
		for (FinderProbMap fpm : map) {
			if (fpm.exists()) {
				bs.addItem( fpm.getDisplayName(), fpm );
			}
		}
	}
	
	@Override
	public Iterator<FinderProbMap> iterator() {
		return map.iterator();
	}

	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {
		
		boolean succ = false;
		for (FinderProbMap fpm : map) {
			if (fpm.doFind(manifestRecorder)) {
				succ = true;
			}
		}
		return succ;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}
}
