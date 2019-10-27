package org.anchoranalysis.gui.finder;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGElemParamsFromImage;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderKeyValueParams;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;

public class FinderNrgStack extends Finder {

	private FinderRasterFolder finderRasterFolder;
	private OperationStackWithParams operationCombined;
	private FinderKeyValueParams finderImageParams;
	private FinderSerializedObject<NRGElemParamsFromImage> finderImageParamsLegacy;
	
	public FinderNrgStack( RasterReader rasterReader, ErrorReporter errorReporter ) {
		finderRasterFolder = new FinderRasterFolder("nrgStack", "nrgStack", rasterReader);
		
		OperationFindNrgStackFromStackCollection nrgStackOperation = new OperationFindNrgStackFromStackCollection(
			new OperationStackCollectionFromFinderRasterFolder(finderRasterFolder)
		);
		this.finderImageParams = new FinderKeyValueParams("nrgStackParams",errorReporter);
		this.finderImageParamsLegacy = new FinderSerializedObject<NRGElemParamsFromImage>(
				"nrgStackImageParams", errorReporter );
		
		operationCombined = new OperationStackWithParams(
			nrgStackOperation,
			finderImageParams,
			finderImageParamsLegacy
		);
	}
	
	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {
		finderImageParams.doFind(manifestRecorder);
		return finderRasterFolder.doFind(manifestRecorder);
	}

	@Override
	public boolean exists() {
		return finderRasterFolder.exists();
	}

	public NRGStackWithParams getNrgStack() throws GetOperationFailedException {
		try {
			return operationCombined.doOperation();
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e);
		}
	}

	public INamedProvider<Stack> getNamedImgStackCollection() throws GetOperationFailedException {
		try {
			return operationCombined.getOperationStackCollection().doOperation( ProgressReporterNull.get() );
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	public Operation<NRGStackWithParams> operationNrgStack() {
		return operationCombined;
	}
	
	public OperationWithProgressReporter<NRGStackWithParams> operationNrgStackWithProgressReporter() {
		return operationCombined;
	}
}
