package org.anchoranalysis.gui.file.interactive;

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


import java.io.File;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.manifest.ManifestDropDown;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.CoupledManifests;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// A file representing the results applied to an image within an executed experiment
public class FileExecutedExperimentImageWithManifest extends InteractiveFile {

	private ManifestDropDown manifestDropDown;
	
	private CoupledManifests coupledManifests;
	private VideoStatsModuleGlobalParams mpg;
	private RasterReader rasterReader;
	private MarkEvaluatorManager markEvaluatorManager;
	
	public FileExecutedExperimentImageWithManifest(
		CoupledManifests coupledManifests,
		RasterReader rasterReader,
		MarkEvaluatorManager markEvaluatorManager,
		VideoStatsModuleGlobalParams mpg
	) {
		this.coupledManifests = coupledManifests;
		this.rasterReader = rasterReader;
		this.mpg = mpg;
		this.markEvaluatorManager = markEvaluatorManager;
	}

	@Override
	public OpenedFile open(
		final IAddVideoStatsModule adder,
		final BoundOutputManagerRouteErrors outputManager		
	) throws OperationFailedException {
		
		manifestDropDown = new ManifestDropDown(coupledManifests);

		try {
			manifestDropDown.init(
				adder,
				rasterReader,
				markEvaluatorManager,
				outputManager,
				mpg
			);
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		return new OpenedFileGUI(this, manifestDropDown.openedFileGUI() );
	}

	@Override
	public String identifier() {
		return coupledManifests.descriptiveName();
	}
	
	@Override
	public File associatedFile() {
		return coupledManifests.pathForBinding().toFile();
	}

	@Override
	public String type() {
		return "experiment results for image";
	}

	
	
	
//	
//
//	
//	@SuppressWarnings("unused")
//	private void addNamedDefinitions( NamedDefinitions parentNamedDefinitions, Iterator<ManifestRecorder> iterator ) {
//		assert(parentNamedDefinitions!=null);
//		while ( iterator.hasNext() ) {
//			ManifestRecorder manifestRecorder = iterator.next();
//			
//			try {
//				FinderSerializedObject<NamedDefinitions> ndFinder = new FinderSerializedObject<NamedDefinitions>("namedDefinitions", logErrorReporter.getErrorReporter());
//				if (ndFinder.doFind(manifestRecorder)) {
//					NamedDefinitions nd = ndFinder.get();
//					assert(nd!=null);
//					parentNamedDefinitions.add(nd);
//				}
//			} catch (GetOperationFailedException e) {
//				logErrorReporter.getErrorReporter().recordError(InteractiveBrowser.class, e);
//			}
//		}
//		 	
//	}
}
