/*-
 * #%L
 * anchor-gui-import
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
package org.anchoranalysis.gui.videostats.modulecreator;

import org.anchoranalysis.core.log.Logger;



import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.feature.evaluator.FeatureEvaluatorTableFrame;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class FeatureEvaluatorCreator extends VideoStatsModuleCreator {

	private FeatureListSrc featureListSrc;
	private Logger logger;
	
	public FeatureEvaluatorCreator( FeatureListSrc featureListSrc, Logger logger) {
		super();
		this.featureListSrc = featureListSrc;
		this.logger = logger;
	}

	public VideoStatsModule createVideoStatsModule(IAddVideoStatsModule adder) throws VideoStatsModuleCreateException {
		
		try {
			FeatureEvaluatorTableFrame mptf = new FeatureEvaluatorTableFrame(
				adder.getSubgroup().getDefaultModuleState().getState(),
				featureListSrc,
				true,
				logger
			);
			return mptf.moduleCreator().createVideoStatsModule( adder.getSubgroup().getDefaultModuleState().getState() );
		} catch (StatePanelUpdateException e) {
			throw new VideoStatsModuleCreateException(e);
		}			
	}
	
	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
			throws VideoStatsModuleCreateException {

		VideoStatsModule module = createVideoStatsModule(adder);
		adder.addVideoStatsModule( module );
	}
	
}
