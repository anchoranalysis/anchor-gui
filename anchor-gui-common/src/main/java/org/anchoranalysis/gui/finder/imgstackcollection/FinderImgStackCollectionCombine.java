package org.anchoranalysis.gui.finder.imgstackcollection;

/*
 * #%L
 * anchor-image-io
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
import java.util.List;

import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;

// Combines a number of other FinderImgStackCollection
public class FinderImgStackCollectionCombine implements FinderImgStackCollection {

	private List<FinderImgStackCollection> list = new ArrayList<>(); 
	
	private CachedOperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> operation =
		new WrapOperationWithProgressReporterAsCached<>(
			pr -> {
				NamedImgStackCollection out = new NamedImgStackCollection();
				
				for( FinderImgStackCollection finder : list) {
					try {
						out.addFrom( finder.getImgStackCollection() );
					} catch (GetOperationFailedException e) {
						throw new OperationFailedException(e);
					}
				}
				
				return out;
			}
		);
	
	@Override
	public NamedProvider<Stack> getImgStackCollection()
			throws GetOperationFailedException {
		try {
			return operation.doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}

	@Override
	public OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> getImgStackCollectionAsOperationWithProgressReporter() {
		return operation;
	}

	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {
		boolean result = false;
		for( FinderImgStackCollection finder : list ) {
			if (finder.doFind(manifestRecorder)) {
				result = true;
			}
		}
		return result;
	}

	// Uses OR behaviour, so returns TRUE if any of the elements exist 
	@Override
	public boolean exists() {
		for( FinderImgStackCollection finder : list ) {
			if (finder.exists()) {
				return true;
			}
		}
		return false;
	}

	public void add( FinderImgStackCollection finder ) {
		list.add( finder );
	}
}
