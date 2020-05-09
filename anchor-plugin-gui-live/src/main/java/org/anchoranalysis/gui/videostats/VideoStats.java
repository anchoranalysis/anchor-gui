package org.anchoranalysis.gui.videostats;

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



import javax.swing.SwingUtilities;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback.ReporterAgg;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.IAggregateReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

// Sends all GUI operations to the edtImpl due to SWING
public class VideoStats extends ReporterAgg<CfgNRGPixelized> {

	//private static Log log = LogFactory.getLog(VideoStats.class);
	
	private VideoStatsEDT edtImpl = new VideoStatsEDT();
	
	public VideoStats() {
		super();
	}
	
	@Override
	public void reportNewBest( final Reporting<CfgNRGPixelized> reporting ) {
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				edtImpl.reportNewBest(reporting);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	@Override
	protected IAggregateReceiver<CfgNRGPixelized> getAggregateReceiver() {
		
		return new IAggregateReceiver<CfgNRGPixelized>() {

			@Override
			public void aggStart( OptimizationFeedbackInitParams<CfgNRGPixelized> initParams, Aggregator agg ) {
				
			}
			
			@Override
			public void aggEnd( Aggregator agg ) {
				
			}
			
			@Override
			public void aggReport( final Reporting<CfgNRGPixelized> reporting, final Aggregator agg ) {

				Runnable r = new Runnable() {

					@Override
					public void run() {
						// We need to copy the aggregator as the original could be updated in the interim
						edtImpl.aggReport(reporting, agg.deepCopy() );
					}
				};
				SwingUtilities.invokeLater(r);
			}
		};
	}
	
	@Override
	public void reportBegin( final OptimizationFeedbackInitParams<CfgNRGPixelized> initParams ) throws ReporterException {
		
		super.reportBegin( initParams );
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					edtImpl.reportBegin(initParams);
				} catch (ReporterException e) {
					initParams.getInitContext().getLogger().getErrorReporter().recordError(VideoStats.class, e);
				}
			}
		};
		SwingUtilities.invokeLater(r);
	}
	
	
	@Override
	public void reportEnd(OptimizationFeedbackEndParams<CfgNRGPixelized> optStep) {
		super.reportEnd(optStep);
	}

	@Override
	public void onInit(MPPInitParams pso) throws InitException {
		super.onInit(pso);
		edtImpl.init( getSharedObjects().getImage(), getLogger() );
	}


}
