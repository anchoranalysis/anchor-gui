package org.anchoranalysis.gui.bean.filecreator;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.input.FeatureListSrcBuilder;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.FeatureEvaluatorCreator;

import ch.ethz.biol.cell.mpp.nrg.nrgscheme.creator.NRGSchemeCreator;

/**
 * Creates a feature-evaluator for a particular list of features 
 * @author FEEHANO
 *
 */
public class FileFeatureEvaluatorCreator extends FileCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField @NonEmpty
	private List<NamedBean<FeatureListProvider>> listFeatures = new ArrayList<NamedBean<FeatureListProvider>>();
	
	@BeanField @Optional
	private NRGSchemeCreator nrgSchemeCreator;
	// END BEAN PROPERTIES
	
	@Override
	public String suggestName() {
		return "untitled feature-evaluator";
	}

	@Override
	public VideoStatsModule createModule(
		String name,
		FileCreatorParams params,
		VideoStatsModuleGlobalParams mpg,
		IAddVideoStatsModule adder,
		IOpenFile fileOpenManager,
		ProgressReporter progressReporter
	) throws VideoStatsModuleCreateException {

		try {
			FeatureEvaluatorCreator creator = new FeatureEvaluatorCreator(
				createSrc(mpg.getLogErrorReporter()),
				mpg.getLogErrorReporter()
			);
			
			return creator.createVideoStatsModule(adder);
			
		} catch (CreateException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}
	
	private FeatureListSrc createSrc( LogErrorReporter logger ) throws CreateException {
		
		FeatureListSrcBuilder builder = new FeatureListSrcBuilder(logger); 
		
		return builder.build(
			createInitParams(logger),
			nrgSchemeCreator
		);
	}
	
	private SharedFeaturesInitParams createInitParams( LogErrorReporter logger ) throws CreateException {
		SharedObjects so = new SharedObjects( logger );
		SharedFeaturesInitParams soFeature = SharedFeaturesInitParams.create(so);
		
		try {
			soFeature.addAll(listFeatures, logger);
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
		return soFeature;
	}

	public List<NamedBean<FeatureListProvider>> getListFeatures() {
		return listFeatures;
	}

	public void setListFeatures(List<NamedBean<FeatureListProvider>> listFeatures) {
		this.listFeatures = listFeatures;
	}

	public NRGSchemeCreator getNrgSchemeCreator() {
		return nrgSchemeCreator;
	}

	public void setNrgSchemeCreator(NRGSchemeCreator nrgSchemeCreator) {
		this.nrgSchemeCreator = nrgSchemeCreator;
	}

}
