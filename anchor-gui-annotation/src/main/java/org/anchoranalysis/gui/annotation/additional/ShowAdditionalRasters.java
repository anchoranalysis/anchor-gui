package org.anchoranalysis.gui.annotation.additional;

/*-
 * #%L
 * anchor-gui-annotation
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

import java.nio.file.Path;
import java.util.List;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;

public class ShowAdditionalRasters {

	private ShowRaster showRaster;
	private List<FilePathGenerator> listFilePathGenerator;
	private Path matchPath;
	private String name;
	private RasterReader rasterReader;
	
	public ShowAdditionalRasters(ShowRaster showRaster, List<FilePathGenerator> listFilePathGenerator, Path matchPath,
			String name, RasterReader rasterReader) {
		super();
		this.showRaster = showRaster;
		this.listFilePathGenerator = listFilePathGenerator;
		this.matchPath = matchPath;
		this.name = name;
		this.rasterReader = rasterReader;
	}
	
	public void apply() throws OperationFailedException {
		
		try {
			// Any additional image windows to be opened
			for( FilePathGenerator filePathGenerator : listFilePathGenerator ) {
				Path rasterPath = filePathGenerator.outFilePath(matchPath, false);
				showRaster.openAndShow( name, rasterPath, rasterReader );
			}
		} catch (AnchorIOException | InitException | GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}
}
