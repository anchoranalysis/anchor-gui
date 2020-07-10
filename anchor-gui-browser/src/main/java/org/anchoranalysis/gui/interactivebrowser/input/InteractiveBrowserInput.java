package org.anchoranalysis.gui.interactivebrowser.input;

import java.nio.file.Path;

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

import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.provider.FilePathProvider;
import org.anchoranalysis.io.input.InputFromManager;

public class InteractiveBrowserInput implements InputFromManager {
	
	private RasterReader rasterReader;
	private List<FileCreator> listFileCreators;
	private NRGSchemeCreator nrgSchemeCreator;
	private List<NamedBean<FeatureListProvider<FeatureInput>>> namedItemSharedFeatureList;
	private List<NamedBean<MarkEvaluator>> namedItemMarkEvaluatorList;
	private List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList;
	private List<NamedBean<FilePathProvider>> namedItemFilePathProviderList;
	private ImporterSettings importerSettings;
	
	public FeatureListSrc createFeatureListSrc(Logger logger) throws CreateException {
		
		SharedObjects so = new SharedObjects(logger);
		KeyValueParamsInitParams soParams = KeyValueParamsInitParams.create(so);
		SharedFeaturesInitParams soFeature = SharedFeaturesInitParams.create(so);
		
		try {
			// Adds the feature-lists to the shared-objects
			soFeature.populate(namedItemSharedFeatureList, logger);
			
			addKeyValueParams( soParams );
			addFilePaths( soParams );
			
			//so.g
		} catch (OperationFailedException e2) {
			throw new CreateException(e2);
		}
		
		return new FeatureListSrcBuilder(logger).build(soFeature, nrgSchemeCreator);
	}
	
	private void addKeyValueParams( KeyValueParamsInitParams soParams ) throws OperationFailedException {
		
		for( NamedBean<KeyValueParamsProvider> ni : this.namedItemKeyValueParamsProviderList ) {
			soParams.getNamedKeyValueParamsCollection().add(
				ni.getName(),
				new OperationCreateFromProvider<>(ni.getValue())
			);
		}
	}
	
	private void addFilePaths( KeyValueParamsInitParams soParams ) throws OperationFailedException {
		
		for( NamedBean<FilePathProvider> ni : this.namedItemFilePathProviderList ) {
			soParams.getNamedFilePathCollection().add(
				ni.getName(),
				new OperationCreateFromProvider<>(ni.getValue())
			);
		}
	}
		
	@Override
	public String descriptiveName() {
		return "interactiveBrowserInput";
	}

	@Override
	public Optional<Path> pathForBinding() {
		return Optional.empty();
	}
	public RasterReader getRasterReader() {
		return rasterReader;
	}

	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}

	public List<FileCreator> getListFileCreators() {
		return listFileCreators;
	}

	public void setListFileCreators(List<FileCreator> listFileCreators) {
		this.listFileCreators = listFileCreators;
	}


	public NRGSchemeCreator getNrgSchemeCreator() {
		return nrgSchemeCreator;
	}


	public void setNrgSchemeCreator(NRGSchemeCreator nrgSchemeCreator) {
		this.nrgSchemeCreator = nrgSchemeCreator;
	}

	public void setNamedItemSharedFeatureList(
			List<NamedBean<FeatureListProvider<FeatureInput>>> namedItemSharedFeatureList) {
		this.namedItemSharedFeatureList = namedItemSharedFeatureList;
	}

	public List<NamedBean<MarkEvaluator>> getNamedItemMarkEvaluatorList() {
		return namedItemMarkEvaluatorList;
	}

	public void setNamedItemMarkEvaluatorList(
			List<NamedBean<MarkEvaluator>> namedItemMarkEvaluatorList) {
		this.namedItemMarkEvaluatorList = namedItemMarkEvaluatorList;
	}

	public List<NamedBean<KeyValueParamsProvider>> getNamedItemKeyValueParamsProviderList() {
		return namedItemKeyValueParamsProviderList;
	}

	public List<NamedBean<FilePathProvider>> getNamedItemFilePathProviderList() {
		return namedItemFilePathProviderList;
	}


	public void setNamedItemFilePathProviderList(
			List<NamedBean<FilePathProvider>> namedItemFilePathProviderList) {
		this.namedItemFilePathProviderList = namedItemFilePathProviderList;
	}


	public void setNamedItemKeyValueParamsProviderList(
			List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList) {
		this.namedItemKeyValueParamsProviderList = namedItemKeyValueParamsProviderList;
	}

	public ImporterSettings getImporterSettings() {
		return importerSettings;
	}

	public void setImporterSettings(ImporterSettings importerSettings) {
		this.importerSettings = importerSettings;
	}
}
