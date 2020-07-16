/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.io.loader.manifest.finder;



import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;

public class FinderScaledOriginal extends FinderRasterStack {
	
	public FinderScaledOriginal(RasterReader rasterReader, ErrorReporter errorReporter) {
		// TODO fix
		// Assumes its a single channel source
		super(rasterReader, errorReporter);
	}
	
	@Override
	protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
			throws MultipleFilesException {
		
		List<FileWrite> scaledOriginalList = FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName("stack_" + ImgStackIdentifiers.INPUT_IMAGE) ); 
		if (scaledOriginalList.size()>1) {
			throw new MultipleFilesException("cannot determine scaledOriginal exactly");
		}
		if (scaledOriginalList.size()==1) {
			return Optional.of(
				scaledOriginalList.get(0)
			);
		}
		
		// We look for an original instead, as maybe scaling isn't used
		List<FileWrite> originalList = FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName("original") );
		
		if (originalList.size()>1) {
			throw new MultipleFilesException("cannot determine original exactly");
		}
		if (originalList.size()==1) {
			return Optional.of(
				originalList.get(0)
			);
		}
		
		return Optional.empty();
	}

}

