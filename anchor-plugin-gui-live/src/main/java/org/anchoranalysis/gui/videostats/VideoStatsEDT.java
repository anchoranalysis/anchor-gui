package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgWithNrgTotalInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;
import org.anchoranalysis.core.bridge.IObjectBridge;

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


import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.ArrayListContainer;
import org.anchoranalysis.core.index.container.BoundChangeEvent;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.IBoundedRangeIncompleteDynamic;
import org.anchoranalysis.core.index.container.BoundChangeEvent.BoundType;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.core.random.RandomNumberGeneratorMersenneTime;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.feature.evaluator.FeatureEvaluatorTableFrame;
import org.anchoranalysis.gui.feature.evaluator.treetable.ExtractFromNamedNRGSchemeSet;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.graph.definition.AcceptanceRateGraphDefinition;
import org.anchoranalysis.gui.graph.definition.CfgSizeGraphDefinition;
import org.anchoranalysis.gui.graph.definition.ExecutionTimeGraphDefinition;
import org.anchoranalysis.gui.graph.definition.NRGGraphDefinition;
import org.anchoranalysis.gui.graph.definition.TemperatureGraphDefinition;
import org.anchoranalysis.gui.graph.visualvm.GraphPanel;
import org.anchoranalysis.gui.graph.visualvm.GraphPanelList;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.addoverlays.AdderAddOverlaysWithStack;
import org.anchoranalysis.gui.videostats.frame.VideoStatsFrame;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameCfgNRGLive;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.init.CreateCombinedStack;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.bean.color.generator.HSBColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.ShuffleColorSetGenerator;
import org.anchoranalysis.io.color.HashedColorSet;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernelList;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;


// Called from VideoStats so that we can run all the GUI operations on the EDT Thread because of Swing
public class VideoStatsEDT {

	// Configurable stats windows, ultimately for beans
	private int windowSize = 300;
	private int numColors = 20;
	// End configurable stats windows, ultimately for beans
	
	// WE HAVE TO SOMEHOW DEALLOCATE THESE
	private ArrayListContainer<CfgWithNrgTotalInstantState> cfgNRGBest;
	private ArrayListContainer<CfgWithNrgTotalInstantState> cfgNRGCurrent;
	
	private GraphPanelList graphPanelList;
	private InternalFrameCfgNRGLive imageFrameCurrent;
	private InternalFrameCfgNRGLive imageFrameBest;
	
	private long startTime;
	
	private VideoStatsFrame videoStatsFrame;
	
	private BoundOutputManagerRouteErrors outputManager;
	
	private BackgroundSet backgroundSet;
	private LogErrorReporter logErrorReporter;

	public void init(ImageInitParams so, LogErrorReporter logger)
			throws InitException {
		
		try {
			backgroundSet = BackgroundSetFactory.createBackgroundSet(
				new WrapStackAsTimeSequence(CreateCombinedStack.apply(so)),
				ProgressReporterNull.get()
			);
			
		} catch (CreateException e) {
			throw new InitException(e);
		}
	}
	
	public void aggReport( Reporting<CfgNRGPixelized> reporting, Aggregator agg ) {
		
		//System.out.printf("Iter=%d  temp=%f\n", optStep.getIter(), agg.getTemp() );
		
		// This means that optStep has already been released, so we skip the final update... can happen due to threading delays
		// We could fix it by doing reference-counting access to the OptimizationSteps, but easier just to skip
		if (reporting.getCfgNRGAfter()==null) {
			return;
		}
		
		CfgWithNrgTotal nrgTotal = reporting.getCfgNRGAfter().getCfgNRG().getCfgWithTotal();
		
		assert( reporting.getCfgNRGAfter() != null );
		assert( nrgTotal != null );
		
		CfgWithNrgTotal cfgWithTotal = nrgTotal.deepCopy();
		
		//System.out.printf("AggReport iter=%d temp=%f\n", optStep.getIter(), optStep.getTemperature() );
		cfgNRGCurrent.add( new CfgWithNrgTotalInstantState( reporting.getIter(), cfgWithTotal ) );
		cfgNRGBest.setMaximumIndex( reporting.getIter() );

		imageFrameCurrent.flush();
		imageFrameBest.flush();
		
		graphPanelList.updateCrnt( reporting.getIter(), getCrntTime(), cfgWithTotal, agg );			
	}
	
	
	public void reportNewBest( Reporting<CfgNRGPixelized> reporting ) {
		
		// There is no longer a CFGNRG After
		if (reporting.getCfgNRGAfter()==null) {
			return;
		}
		
		assert( reporting.getCfgNRGAfter() != null );
		assert( reporting.getCfgNRGAfter().getCfgNRG().getCfgWithTotal() != null );
		
		CfgWithNrgTotal afterCopy = reporting.getCfgNRGAfter().getCfgNRG().getCfgWithTotal().deepCopy();
		
		cfgNRGBest.add(
			new CfgWithNrgTotalInstantState( reporting.getIter(), afterCopy  )
		);
		graphPanelList.updateBest(
			reporting.getIter(),
			getCrntTime(),
			afterCopy
		);
	}
	
	private GraphPanelList createGraphPanelList( WeightedKernelList<?> kernelFactoryList ) {
		GraphPanelList graphPanelList = new GraphPanelList();
		graphPanelList.add( new GraphPanel( new NRGGraphDefinition(windowSize) ) );
		graphPanelList.add( new GraphPanel( new CfgSizeGraphDefinition(windowSize) ) );
		graphPanelList.add( new GraphPanel( new ExecutionTimeGraphDefinition(windowSize) ));
		graphPanelList.add( new GraphPanel( new TemperatureGraphDefinition(windowSize) ));
		graphPanelList.add( new GraphPanel( new AcceptanceRateGraphDefinition(windowSize, kernelFactoryList ) ));
		return graphPanelList;
	}
	
	public void reportBegin( OptimizationFeedbackInitParams<CfgNRGPixelized> initParams ) throws ReporterException {
		
		this.outputManager = initParams.getInitContext().getOutputManager();
		this.logErrorReporter = initParams.getInitContext().getLogger();
		
		// Then we want to shut down the previous
		if (videoStatsFrame!=null) {
			videoStatsFrame.setVisible(false);
			videoStatsFrame = null;
		}
		
		
		NRGStackWithParams nrgStack = initParams.getInitContext().getDualStack().getNrgStack();

		
		
		cfgNRGBest = new ArrayListContainer<>();
		cfgNRGCurrent = new ArrayListContainer<>();
		
		assert( initParams.getInitContext().getExperimentDescription() != null );
		
		videoStatsFrame = new VideoStatsFrame( String.format("live mode - %s", initParams.getInitContext().getExperimentDescription() ) );
		videoStatsFrame.initBeforeAddingFrames( logErrorReporter.getErrorReporter() );
		
		imageFrameCurrent = new InternalFrameCfgNRGLive("current");
		imageFrameBest = new InternalFrameCfgNRGLive("best");
		
		graphPanelList = createGraphPanelList( initParams.getKernelFactoryList() );
		
		// Let's open with no configuration
		Cfg cfgEmpty = new Cfg();
		CfgWithNrgTotal cfgNRGEmpty = new CfgWithNrgTotal( cfgEmpty, initParams.getInitContext().getNrgScheme());
		
		//DefaultModuleState defaultState = videoStatsFrame.createCurrentFrameState();
		//defaultState.setFrameIndex(0);
		
		CfgWithNrgTotalInstantState initialState = new CfgWithNrgTotalInstantState( 0, cfgNRGEmpty ); 
		cfgNRGCurrent.add(initialState);
		cfgNRGBest.add(initialState);
		
		
		startTime = System.currentTimeMillis();
		
		ColorIndex colorIndex;
		try {
			colorIndex = new HashedColorSet( new ShuffleColorSetGenerator( new HSBColorSetGenerator() ), numColors );
		} catch (OperationFailedException e1) {
			throw new ReporterException(e1);
		}
		
		SequenceMemory sequenceMemory = new SequenceMemory();
		ExportPopupParams popUpParams = new ExportPopupParams( logErrorReporter.getErrorReporter() );
		assert( outputManager!=null );
		popUpParams.setOutputManager( outputManager );
		popUpParams.setParentFrame( videoStatsFrame );
		popUpParams.setSequenceMemory( sequenceMemory );


		VideoStatsModuleGlobalParams mpg = new VideoStatsModuleGlobalParams();
		mpg.setExportPopupParams( popUpParams );
		mpg.setLogErrorReporter( logErrorReporter );
		mpg.setThreadPool( videoStatsFrame.getThreadPool() );
		mpg.setRandomNumberGenerator( new RandomNumberGeneratorMersenneTime() );
		mpg.setExportTaskList( new ExportTaskList() );
		mpg.setDefaultColorIndexForMarks(colorIndex);
		mpg.setGraphicsCurrentScreen( videoStatsFrame.getGraphicsConfiguration() );
			
		//BackgroundSetUtilities.addFromImgStackCollection(backgroundSet, stackCollection);
	
		VideoStatsModuleSubgroup liveSubgroup = new VideoStatsModuleSubgroup( new DefaultModuleState() );
		

		// The default background for our frames
		{
			IObjectBridge<Integer,DisplayStack> initialBackground = null;
			try {
				initialBackground = backgroundSet.stackCntr( ImgStackIdentifiers.INPUT_IMAGE_VISUAL );
				
				// If there's no initial background
				if (initialBackground==null) {
					initialBackground = backgroundSet.stackCntr( ImgStackIdentifiers.INPUT_IMAGE );
				}
			} catch (GetOperationFailedException e) {
				throw new ReporterException(e);
			}
			liveSubgroup.getDefaultModuleState().getLinkStateManager().setBackground( initialBackground );
		}
		
		
		// The default slice - the middle one
		if (nrgStack.getDimensions().getZ() > 1) {
			liveSubgroup.getDefaultModuleState().getLinkStateManager().setSliceNum( nrgStack.getDimensions().getZ() / 2 );
		}

		
		//IAddVideoStatsModule adder = new IAddVideoStatsModule();
		
		IAddVideoStatsModule adder = new SubgrouppedAdder(videoStatsFrame, liveSubgroup.getDefaultModuleState().getState() );
		adder = new AdderAddOverlaysWithStack(adder, videoStatsFrame.getThreadPool(), logErrorReporter.getErrorReporter());
		
		{
			//GetNrgStackOperation getImageOperation = new GetNrgStackOperation( nrgStack );
				
			adder = new AdderAppendNRGStack(
				adder,
				() -> nrgStack
			);
		}
		
		
		
		try {
			ISliderState sliderState = imageFrameCurrent.init(
				cfgNRGCurrent,
				liveSubgroup.getDefaultModuleState().getState(),
				backgroundSet,
				mpg
			);
			adder.addVideoStatsModule(
				imageFrameCurrent.moduleCreator(sliderState).createVideoStatsModule(
					liveSubgroup.getDefaultModuleState().getState()
				)
			);
			
		} catch (VideoStatsModuleCreateException e) {
			logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
		} catch (InitException e) {
			logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
		}
		
		try {
			ISliderState sliderState = imageFrameBest.init(
				cfgNRGBest,
				liveSubgroup.getDefaultModuleState().getState(),
				backgroundSet,
				mpg
			);
			adder.addVideoStatsModule(
				imageFrameBest.moduleCreator(sliderState).createVideoStatsModule(
					liveSubgroup.getDefaultModuleState().getState()
				)
			);
		} catch (VideoStatsModuleCreateException e) {
			logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
		} catch (InitException e) {
			logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
		}
		
		for (GraphPanel gp : graphPanelList) {
			try {
				adder.addVideoStatsModule(
					moduleCreator(gp).createVideoStatsModule(
						liveSubgroup.getDefaultModuleState().getState()
					)
				);
			} catch (VideoStatsModuleCreateException e) {
				logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
			}
		}

		try {
			addMarkPropertiesTableFrame(
				initParams.getInitContext().getNrgScheme(),
				liveSubgroup.getDefaultModuleState().getState(),
				adder
			);
		} catch (VideoStatsModuleCreateException e) {
			logErrorReporter.getErrorReporter().recordError(VideoStatsEDT.class, e);
		}
		
		cfgNRGCurrent.addBoundChangeListener( new FrameBoundsChanged( cfgNRGCurrent ) );
		
		videoStatsFrame.getToolbar().addSeparator();
		
		videoStatsFrame.getToolbar().add(
				new TerminateAction(initParams.getInitContext().getTriggerTerminationCondition(),
					new IconFactory().icon("/toolbarIcon/terminate.png")	)
		);
		
		videoStatsFrame.showWithDefaultView();

		//SwingUtilities.invokeLater(r);
	}
	
	

	private static IModuleCreatorDefaultState moduleCreator( GraphPanel panel ) {
		return defaultFrameState-> {
		
			VideoStatsModule module = new VideoStatsModule();
			
			module.setComponent( panel.getContainingFrame() );
			module.setFixedSize( false );
	
			return module;
		};
	}
	
	private static NamedNRGSchemeSet createNamedNRGSchemeFromFeatures( NRGSchemeWithSharedFeatures nrgScheme ) {
		
		NamedNRGSchemeSet nrgElemSet = new NamedNRGSchemeSet(
			nrgScheme.getSharedFeatures().downcast()
		); 
		nrgElemSet.add("lastExecution", nrgScheme.getNrgScheme() );
		return nrgElemSet;
	}
	
	
	private void addMarkPropertiesTableFrame( NRGSchemeWithSharedFeatures nrgScheme, final DefaultModuleState defaultState, final IAddVideoStatsModule adder ) throws VideoStatsModuleCreateException {

		try {
			FeatureListSrc src = new ExtractFromNamedNRGSchemeSet(
				createNamedNRGSchemeFromFeatures( nrgScheme )
			);
			
			FeatureEvaluatorTableFrame markPropertiesTableFrame = new FeatureEvaluatorTableFrame(
				defaultState,
				src,
				false,
				logErrorReporter
			);
			adder.addVideoStatsModule( markPropertiesTableFrame.moduleCreator().createVideoStatsModule(defaultState) );
		} catch (StatePanelUpdateException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}
	
	private long getCrntTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	
	private class FrameBoundsChanged implements BoundChangeListener {
		
		private IBoundedRangeIncompleteDynamic frameBounds;
		private int prevBoundMax;
		
		public FrameBoundsChanged( IBoundedRangeIncompleteDynamic frameBounds ) {
			this.prevBoundMax = frameBounds.getMaximumIndex();
			this.frameBounds = frameBounds;
		}
		
		@Override
		public void boundChanged(BoundChangeEvent e) {
			
			if (e.getBoundType()==BoundType.MAXIMUM) {
				
				int maxIndex = frameBounds.getMaximumIndex();
				
				if (videoStatsFrame.getLastFrameIndex()==prevBoundMax) {
					videoStatsFrame.setFrameIndex(maxIndex);
				}
				prevBoundMax = maxIndex; 
			}
			
			
		}
	}



}