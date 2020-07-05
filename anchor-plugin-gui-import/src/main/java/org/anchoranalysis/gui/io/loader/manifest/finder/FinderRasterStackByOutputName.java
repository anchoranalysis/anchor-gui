package org.anchoranalysis.gui.io.loader.manifest.finder;

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


import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;

public class FinderRasterStackByOutputName extends FinderRasterStack {

	private String outputName;
	
	public FinderRasterStackByOutputName(RasterReader rasterReader, String outputName, ErrorReporter errorReporter ) {

		// TODO fix
		// Assumes its a trebble channel source
		super(rasterReader, errorReporter);
		this.outputName = outputName;
	}

	@Override
	protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
			throws MultipleFilesException {
		
		List<FileWrite> list = FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName(outputName) ); 
		if (list.size()>1) {
			throw new MultipleFilesException("cannot determine " + outputName + " exactly");
		}
		if (list.size()==1) {
			return Optional.of(
				list.get(0)
			);
		}
		
		return Optional.empty();
	}

}
