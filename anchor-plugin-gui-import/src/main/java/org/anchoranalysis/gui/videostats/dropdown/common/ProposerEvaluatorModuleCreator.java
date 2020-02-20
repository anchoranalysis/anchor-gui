package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.error.InitException;

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


import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundUpdater;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFromExisting;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.IUpdatableMarkEvaluator;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.InternalFrameMarkProposerEvaluator;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

class ProposerEvaluatorModuleCreator extends VideoStatsModuleCreator {
	
	private MarkEvaluatorSetForImage markEvaluatorSet;
	private NRGBackground nrgBackground;
	private OutputWriteSettings outputWriteSettings;
	private IUpdatableMarkEvaluator markEvaluatorUpdater;
	private VideoStatsModuleGlobalParams mpg;
	
	public ProposerEvaluatorModuleCreator(
			MarkEvaluatorSetForImage markEvaluatorSet,
			NRGBackground nrgBackground,
			OutputWriteSettings outputWriteSettings,
			IUpdatableMarkEvaluator markEvaluatorUpdater,
			VideoStatsModuleGlobalParams mpg			
		) {
		super();
		this.markEvaluatorSet = markEvaluatorSet;
		this.nrgBackground = nrgBackground;
		this.outputWriteSettings = outputWriteSettings;
		this.mpg = mpg;
		this.markEvaluatorUpdater = markEvaluatorUpdater;
	}


	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder) throws VideoStatsModuleCreateException {
		
		try {
			InternalFrameMarkProposerEvaluator imageFrame = new InternalFrameMarkProposerEvaluator(
				mpg.getLogErrorReporter().getErrorReporter()
			);
			
			// Configure initial-size based upon overall window size
			imageFrame.controllerImageView().configure(
				0.8,
				0.6,
				0,
				50,
				mpg.getGraphicsCurrentScreen()
			);
			
			// Here we optionally set an adder to send back nrg_stacks
			ISliderState sliderState = imageFrame.init(
				markEvaluatorSet,
				adder.getSubgroup().getDefaultModuleState().getState(),
				new IdentityOperationWithProgressReporter<>(adder),
				nrgBackground.getBackgroundSet(),
				outputWriteSettings,
				mpg
			);
			
			IBackgroundUpdater backgroundUpdater = imageFrame.controllerBackgroundMenu().add(
				mpg,
				nrgBackground.getBackgroundSet()
			);
			
			imageFrame.addMarkEvaluatorChangedListener( e -> {

				if (e.getMarkEvaluator()!=null) {
					backgroundUpdater.update(
						new CreateBackgroundSetFromExisting(
							nrgBackground.getBackgroundSet(),
							e.getMarkEvaluator().getProposerSharedObjectsOperation(),
							outputWriteSettings
						)
					);
					markEvaluatorUpdater.setMarkEvaluatorIdentifier( e.getMarkEvaluatorName() );
				} else {
					
					backgroundUpdater.update( nrgBackground.getBackgroundSet() );
					markEvaluatorUpdater.setMarkEvaluatorIdentifier(null);
				}
			});
			
			ModuleAddUtilities.add(adder, imageFrame.moduleCreator(), sliderState);

		} catch (VideoStatsModuleCreateException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ProposerEvaluatorModuleCreator.class, e);
		} catch (InitException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ProposerEvaluatorModuleCreator.class, e);
		}		
	}
}