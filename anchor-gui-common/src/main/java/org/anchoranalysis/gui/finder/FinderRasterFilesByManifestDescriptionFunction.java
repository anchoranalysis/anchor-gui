package org.anchoranalysis.gui.finder;

import java.nio.file.Path;

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


import java.util.List;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;

public class FinderRasterFilesByManifestDescriptionFunction extends Finder {

	private String function;
	
	private List<FileWrite> list;
	
	private RasterReader rasterReader;
	
	public FinderRasterFilesByManifestDescriptionFunction(RasterReader rasterReader, String function ) {

		this.function = function;
		this.rasterReader = rasterReader;
	}


	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {

		ManifestDescriptionMatchAnd matchManifest = new ManifestDescriptionMatchAnd();
		matchManifest.addCondition( new ManifestDescriptionFunctionMatch(function));
		matchManifest.addCondition( new ManifestDescriptionTypeMatch("raster"));
		
		list = FinderUtilities.findListFile(manifestRecorder,  new FileWriteManifestMatch(matchManifest) );
		return list!=null && list.size()>0;
	}

	@Override
	public boolean exists() {
		return list!=null && list.size()>0;
	}

	
	public NamedImgStackCollection createStackCollection() throws RasterIOException {
		
		 NamedImgStackCollection out = new  NamedImgStackCollection();
		 for( FileWrite fileWrite : list) {
			 	String name = fileWrite.getIndex();
			 
				// Assume single series, single channel
				Path filePath = fileWrite.calcPath();
								
				out.addImageStack(
					name,
					new CachedOpenStackOp(filePath, rasterReader)
				);
		 }
		 return out;
	}
	
	
	private static class CachedOpenStackOp extends CachedOperationWithProgressReporter<Stack> {

		private Path filePath;
		private RasterReader rasterReader;
				
		public CachedOpenStackOp(Path filePath, RasterReader rasterReader) {
			super();
			this.filePath = filePath;
			this.rasterReader = rasterReader;
		}

		@Override
		protected Stack execute(ProgressReporter progressReporter) throws ExecuteException {
			try (OpenedRaster openedRaster = rasterReader.openFile(filePath)) {
				return openedRaster.open(0, progressReporter).get(0);
				
			} catch (RasterIOException e) {
				throw new ExecuteException(e);
			}
		}
		
	}
	
}
