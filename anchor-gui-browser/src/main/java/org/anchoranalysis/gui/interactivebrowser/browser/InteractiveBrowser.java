package org.anchoranalysis.gui.interactivebrowser.browser;



/*-
 * #%L
 * anchor-gui-browser
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

import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.init.GeneralInitParams;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.mpp.MarkEvaluator;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.gui.interactivebrowser.openfile.FileCreatorLoader;
import org.anchoranalysis.gui.interactivebrowser.openfile.OpenFile;
import org.anchoranalysis.gui.interactivebrowser.openfile.OpenFileTypeFactory;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.frame.VideoStatsFrame;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.color.generator.HSBColorSetGenerator;
import org.anchoranalysis.io.bean.color.generator.ShuffleColorSetGenerator;
import org.anchoranalysis.io.color.HashedColorSet;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;


public class InteractiveBrowser {

	private VideoStatsFrame videoStatsFrame;
	
	private int numColors = 20;
	
	private BoundOutputManagerRouteErrors outputManager;
	
	private ExportTaskList exportTaskList;
	
	// How long the splash screen displays for
	private int SplashScreenTime = 2000;
	
	private CacheMonitor cacheMonitor = new CacheMonitor();
	
	// Manages the available mark evaluators
	private MarkEvaluatorManager markEvaluatorManager;
	
	private OpenFile openFileCreator;
	
	private OpenFileTypeFactory openFileTypeFactory;
	
	private GeneralInitParams paramsGeneral;
	
	public InteractiveBrowser(BoundOutputManagerRouteErrors outputManager, GeneralInitParams paramsGeneral, ExportTaskList exportTaskList ) {
		super();
		this.outputManager = outputManager;
		this.exportTaskList = exportTaskList;
		this.paramsGeneral = paramsGeneral;
	}
	
	public void init(
		InteractiveBrowserInput interactiveBrowserInput
	) throws InitException {
		
		videoStatsFrame = new VideoStatsFrame( "Interactive Browser" );

		openFileTypeFactory = new OpenFileTypeFactory(
			interactiveBrowserInput.getImporterSettings().getOpenFileImporters()
		);
		
		displaySplashScreen();
	
		setup( interactiveBrowserInput );
	}
	
	private void setup( InteractiveBrowserInput interactiveBrowserInput ) throws InitException {
		SubgrouppedAdder globalSubgroupAdder = new SubgrouppedAdder(videoStatsFrame,new DefaultModuleState());
		
		VideoStatsModuleGlobalParams moduleParams = createModuleParams(
			createExportPopupParams(),
			createColorIndex()
		);
		
		initMarkEvaluatorManager(interactiveBrowserInput);
		
		addGUIComponents(
			interactiveBrowserInput,
			moduleParams,
			globalSubgroupAdder,
			createFileOpenManager(globalSubgroupAdder)
		);
	}
	
	private FileOpenManager createFileOpenManager( SubgrouppedAdder globalSubgroupAdder ) {
		return new FileOpenManager(
			globalSubgroupAdder,
			videoStatsFrame,
			outputManager
		);
	}
	
	private void initMarkEvaluatorManager( InteractiveBrowserInput interactiveBrowserInput ) {
		markEvaluatorManager = new MarkEvaluatorManager(paramsGeneral);
		
		if (interactiveBrowserInput.getNamedItemMarkEvaluatorList()!=null) {
			for( NamedBean<MarkEvaluator> ni : interactiveBrowserInput.getNamedItemMarkEvaluatorList()) {
				markEvaluatorManager.add(ni.getName(), ni.getValue());;
			}
		}
	}
	
	private void addGUIComponents(
		InteractiveBrowserInput interactiveBrowserInput,
		VideoStatsModuleGlobalParams moduleParams,
		SubgrouppedAdder globalSubgroupAdder,
		FileOpenManager fileOpenManager
	) throws InitException {
		
		FeatureListSrc featureListSrc;
		try {
			featureListSrc = interactiveBrowserInput.createFeatureListSrc(
				outputManager,
				paramsGeneral.getLogErrorReporter()
			);
		} catch (CreateException e) {
			throw new InitException(e);
		} 
		
		AdderWithNrg adderWithNrg = new AdderWithNrg(
			moduleParams,
			featureListSrc,
			globalSubgroupAdder
		);
						
		FileCreatorLoader fileCreatorLoader = creatorLoader(
			adderWithNrg,
			interactiveBrowserInput.getRasterReader(),
			fileOpenManager,
			interactiveBrowserInput.getImporterSettings()
		);
		
		openFileCreator = new OpenFile(
			videoStatsFrame,
			fileCreatorLoader,
			openFileTypeFactory,
			paramsGeneral.getLogErrorReporter()
		);
		videoStatsFrame.getListFileActions().add( openFileCreator );
					
		
		// We add the GUI components
		addGUIComponentsInner( adderWithNrg, fileCreatorLoader, fileOpenManager, interactiveBrowserInput.getListFileCreators() );
	}
	
	private FileCreatorLoader creatorLoader(
		AdderWithNrg adderWithNrg,
		RasterReader rasterReader,
		FileOpenManager fileOpenManager,
		ImporterSettings importerSettings
	) {
		return adderWithNrg.createFileCreatorLoader(
			rasterReader,
			fileOpenManager,
			markEvaluatorManager,
			cacheMonitor,
			importerSettings,
			videoStatsFrame.getLastMarkDisplaySettings()
		);
	}
	
	public void showWithDefaultView() {
		videoStatsFrame.showWithDefaultView();
	}
	
	private void addGUIComponentsInner( AdderWithNrg adderNrg, FileCreatorLoader fileCreatorLoader, FileOpenManager fileOpenManager, List<FileCreator> fileCreators ) throws InitException {
		
		videoStatsFrame.getToolbar().add( openFileCreator);
		videoStatsFrame.getToolbar().addSeparator();
		
		videoStatsFrame.initBeforeAddingFrames( paramsGeneral.getErrorReporter() );
		
		videoStatsFrame.getToolbar().addSeparator();
		
		adderNrg.addGlobalSet(videoStatsFrame.getToolbar());
		
		videoStatsFrame.getToolbar().addSeparator();
		
		videoStatsFrame.getToolbar().addSeparator();

		
		// We maintain a mapping between modules and 
		videoStatsFrame.addVideoStatsModuleClosedListener( evt -> {
			assert( evt.getModule()!=null );
			fileOpenManager.closeModule(evt.getModule());
			videoStatsFrame.selectFrame(true);
		});

		fileCreatorLoader.addFileListSummaryModule( fileCreators, videoStatsFrame );
		
		videoStatsFrame.setDropTarget(
			new CustomDropTarget(
				openFileCreator,
				videoStatsFrame,
				fileCreatorLoader.getImporterSettings(),
				paramsGeneral.getErrorReporter()
			)
		);
		
	}

	private ExportPopupParams createExportPopupParams() {
		SequenceMemory sequenceMemory = new SequenceMemory();
		ExportPopupParams popUpParams = new ExportPopupParams(
			paramsGeneral.getErrorReporter()
		);
		assert( outputManager!= null );
		popUpParams.setOutputManager( outputManager );
		popUpParams.setParentFrame( videoStatsFrame );
		popUpParams.setSequenceMemory( sequenceMemory );
		popUpParams.setCacheMonitor(cacheMonitor);
		return popUpParams;
	}

	private VideoStatsModuleGlobalParams createModuleParams( ExportPopupParams popUpParams, ColorIndex colorIndex ) {
		VideoStatsModuleGlobalParams moduleParams = new VideoStatsModuleGlobalParams();
		moduleParams.setExportPopupParams(popUpParams);
		moduleParams.setLogErrorReporter( paramsGeneral.getLogErrorReporter() );
		moduleParams.setThreadPool(videoStatsFrame.getThreadPool());
		moduleParams.setRandomNumberGenerator( paramsGeneral.getRe() );
		moduleParams.setExportTaskList(exportTaskList);
		moduleParams.setDefaultColorIndexForMarks(colorIndex);
		moduleParams.setGraphicsCurrentScreen( videoStatsFrame.getGraphicsConfiguration() );
		return moduleParams;
	}
	
	private ColorIndex createColorIndex() throws InitException {
		try {
			return new HashedColorSet( new ShuffleColorSetGenerator( new HSBColorSetGenerator() ), numColors );
		} catch (OperationFailedException e) {
			throw new InitException(e);
		}
	}

	private void displaySplashScreen() {
		// Display splash scren
		new SplashScreenTime(
			"/appSplash/anchor_splash.png",
			videoStatsFrame,
			SplashScreenTime,
			paramsGeneral.getErrorReporter()
		);
	}
}
