package org.anchoranalysis.gui.bean.filecreator;



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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileExecutedExperimentImageWithManifest;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.plugin.io.bean.input.manifest.CoupledManifestsInputManager;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;
import org.anchoranalysis.plugin.io.manifest.ManifestCouplingDefinition;

// TODO duplication not right
public class ExecutedExperimentFileCreator extends FileCreatorGeneralList {

	// START BEANS
	@BeanField
	private CoupledManifestsInputManager coupledManifestsInputManager;
	// END BEANS
	
	private List<String> experimentNames;

	public CoupledManifestsInputManager getCoupledManifestsInputManager() {
		return coupledManifestsInputManager;
	}

	public void setCoupledManifestsInputManager(
			CoupledManifestsInputManager coupledManifestsInputManager) {
		this.coupledManifestsInputManager = coupledManifestsInputManager;
	}

	@Override
	public void addFilesToList(List<InteractiveFile> listFiles, FileCreatorParams params, ProgressReporter progressReporter) throws OperationFailedException {
		
		// TODO for now, we have no features from the NamedDefinitons added to the global feature list
		
		ManifestCouplingDefinition manifestCouplingDefinition;
		try {
			manifestCouplingDefinition = coupledManifestsInputManager.manifestCouplingDefinition(
				new InputManagerParams(
					params.createInputContext(),
					progressReporter,
					params.getLogErrorReporter()
				)
			);
		} catch (DeserializationFailedException e) {
			throw new OperationFailedException(e);
		}
		
		experimentNames = new ArrayList<>();

		
		for (Iterator<ManifestRecorder> itrExp = manifestCouplingDefinition.iteratorExperimentalManifests(); itrExp.hasNext();) {
			ManifestRecorder manifestExperiment = itrExp.next();
			addVideoStatsFileFromManifestExperiment( manifestExperiment, params, manifestCouplingDefinition, listFiles );
		}
		
		for (Iterator<CoupledManifests> itrCM = manifestCouplingDefinition.iteratorCoupledManifests(); itrCM.hasNext(); ) {
			CoupledManifests cm = itrCM.next();
			
			// We are only interested in coupledmanifests that we did not find previously
			if (!cm.getExperimentManifest().isPresent()) {
				addVideoStatsFileToList(cm, params, listFiles);
			}
		}
	}
	
	private void addVideoStatsFileFromManifestExperiment( ManifestRecorder manifestExperiment, FileCreatorParams params, ManifestCouplingDefinition manifestCouplingDefinition, List<InteractiveFile> listFiles ) {
		experimentNames.add( manifestExperiment.getRootFolder().getRelativePath().toString() );
		
		FinderSerializedObject<NRGScheme> finderNRGScheme = new FinderSerializedObject<>("nrgScheme", params.getLogErrorReporter().errorReporter() );
		finderNRGScheme.doFind(manifestExperiment);
		
		for (Iterator<CoupledManifests> i = manifestCouplingDefinition.iteratorCoupledManifestsFor(manifestExperiment); i.hasNext(); ) 	{
			CoupledManifests coupledManifests = i.next();
			addVideoStatsFileToList(coupledManifests, params, listFiles);
		}
	}
	
	private void addVideoStatsFileToList(
		CoupledManifests coupledManifests,
		FileCreatorParams params,
		List<InteractiveFile> listFiles
	) {
		
		FileExecutedExperimentImageWithManifest file = new FileExecutedExperimentImageWithManifest(
			coupledManifests,
			params.getRasterReader(),
			params.getMarkCreatorParams()
		);

		listFiles.add(file);		
	}
	
	@Override
	public String suggestName() {
		
		if (hasCustomName()) {
			return getCustomName();
		}
		
		if (experimentNames.isEmpty()) {
			return "Untitled Executed Experiment Files";
		} else if (experimentNames.size()==1) {
			return experimentNames.get(0);
		} else {
			return "Combined Executed Experiment Files";
		}
	}
}
