package org.anchoranalysis.gui.interactivebrowser.launch;

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
import java.util.List;

import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.provider.FilePathProvider;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;

public class InteractiveBrowserInputManager extends InputManager<InteractiveBrowserInput>  {

	// START BEAN PROPERTIES
	@BeanField
	private List<FileCreator> listFileCreators = new ArrayList<>();
	
	@BeanField @DefaultInstance
	private RasterReader rasterReader;
	
	@BeanField @OptionalBean
	private NRGSchemeCreator nrgSchemeCreator;
	
	@BeanField
	private List<NamedBean<FeatureListProvider<FeatureInput>>> namedItemSharedFeatureList = new ArrayList<>();
	
	@BeanField
	private List<NamedBean<MarkEvaluator>> namedItemMarkEvaluatorList = new ArrayList<>();
	
	@BeanField @OptionalBean
	private List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList = new ArrayList<>();
	
	@BeanField @OptionalBean
	private List<NamedBean<FilePathProvider>> namedItemFilePathProviderList = new ArrayList<>();
	
	@BeanField
	private ImporterSettings importerSettings;
	// END BEAN PROPERTIES

	@Override
	public List<InteractiveBrowserInput> inputObjects(InputManagerParams params)
			throws AnchorIOException {
		
		InteractiveBrowserInput ibi = new InteractiveBrowserInput();
		ibi.setListFileCreators( listFileCreators );
		ibi.setRasterReader( rasterReader );
		ibi.setNrgSchemeCreator( nrgSchemeCreator );
		ibi.setNamedItemSharedFeatureList( namedItemSharedFeatureList );
		ibi.setNamedItemMarkEvaluatorList(namedItemMarkEvaluatorList);
		ibi.setNamedItemKeyValueParamsProviderList(namedItemKeyValueParamsProviderList);
		ibi.setNamedItemFilePathProviderList(namedItemFilePathProviderList);
		ibi.setImporterSettings(importerSettings);
		
		return singletonList(ibi);
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

	public List<NamedBean<FeatureListProvider<FeatureInput>>> getNamedItemSharedFeatureList() {
		return namedItemSharedFeatureList;
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

	public void setNamedItemKeyValueParamsProviderList(
			List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList) {
		this.namedItemKeyValueParamsProviderList = namedItemKeyValueParamsProviderList;
	}
	public List<NamedBean<FilePathProvider>> getNamedItemFilePathProviderList() {
		return namedItemFilePathProviderList;
	}

	public void setNamedItemFilePathProviderList(
			List<NamedBean<FilePathProvider>> namedItemFilePathProviderList) {
		this.namedItemFilePathProviderList = namedItemFilePathProviderList;
	}
		
	private static <T> List<T> singletonList( T elem ) {
		List<T> singletonList = new ArrayList<>();
		singletonList.add(elem);
		return singletonList;
	}

	public ImporterSettings getImporterSettings() {
		return importerSettings;
	}

	public void setImporterSettings(ImporterSettings importerSettings) {
		this.importerSettings = importerSettings;
	}

}
