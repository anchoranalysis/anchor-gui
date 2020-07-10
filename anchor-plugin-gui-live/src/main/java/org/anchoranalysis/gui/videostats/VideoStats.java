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
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.AggregateReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

// Sends all GUI operations to the edtImpl due to SWING
public class VideoStats extends ReporterAgg<CfgNRGPixelized> {
	
	private VideoStatsEDT edtImpl = new VideoStatsEDT();
	
	@Override
	public void reportNewBest( final Reporting<CfgNRGPixelized> reporting ) {
		SwingUtilities.invokeLater(
			() -> edtImpl.reportNewBest(reporting) 
		);
	}

	@Override
	protected AggregateReceiver<CfgNRGPixelized> getAggregateReceiver() {
		// We need to copy the aggregator as the original could be updated in the interim
		return (Reporting<CfgNRGPixelized> reporting, Aggregator agg) -> SwingUtilities.invokeLater(
			() -> edtImpl.aggReport(reporting, agg.deepCopy() ) 
		);
	}
	
	@Override
	public void reportBegin( final OptimizationFeedbackInitParams<CfgNRGPixelized> initParams ) throws ReporterException {
		super.reportBegin( initParams );

		SwingUtilities.invokeLater(
			() -> {
				try {
					edtImpl.reportBegin(initParams);
				} catch (ReporterException e) {
					initParams.getInitContext().getLogger().errorReporter().recordError(VideoStats.class, e);
				}				
			}
		);
	}

	@Override
	public void onInit(MPPInitParams pso) throws InitException {
		super.onInit(pso);
		edtImpl.init( getInitializationParameters().getImage(), getLogger() );
	}
}
