package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
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
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}

	public NamedProvider<Stack> getNamedImgStackCollection() throws GetOperationFailedException {
		try {
			return operationCombined.getOperationStackCollection().doOperation( ProgressReporterNull.get() );
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	public Operation<NRGStackWithParams,OperationFailedException> operationNrgStack() {
		return operationCombined;
	}
	
	public OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> operationNrgStackWithProgressReporter() {
		return operationCombined;
	}
}
