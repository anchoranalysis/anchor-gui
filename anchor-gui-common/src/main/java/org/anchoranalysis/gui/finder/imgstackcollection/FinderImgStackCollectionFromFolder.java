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


import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.finder.FinderRasterFolder;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;

// Finds an image stack collection
public class FinderImgStackCollectionFromFolder extends FinderImgStackCollection {

	private FinderRasterFolder delegate;
	
	private CachedOperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> operationImgStackCollection =
		new WrapOperationWithProgressReporterAsCached<>(
			pr -> {
				try {
					return delegate.createStackCollection(false);
				} catch (CreateException e) {
					throw new OperationFailedException(e);
				}
			}
		);
	
	
	public FinderImgStackCollectionFromFolder( RasterReader rasterReader, String folderName ) {
		delegate = new FinderRasterFolder(folderName, "stackFromCollection", rasterReader );
	}

	@Override
	public NamedProvider<Stack> getImgStackCollection() throws GetOperationFailedException {
		try {
			return operationImgStackCollection.doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	@Override
	public OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> getImgStackCollectionAsOperationWithProgressReporter() {
		return operationImgStackCollection;
	}
	
	public OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> getImgStackCollectionAsOperation() {
		return operationImgStackCollection;
	}

	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {
		return delegate.doFind(manifestRecorder);
	}

	@Override
	public boolean exists() {
		return delegate.exists();
	}

}
