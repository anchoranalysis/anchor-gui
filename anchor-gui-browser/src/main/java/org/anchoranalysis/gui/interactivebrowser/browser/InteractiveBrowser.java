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

import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.core.random.RandomNumberGeneratorMersenne;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
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
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;


public class InteractiveBrowser {
	
	// How long the splash screen displays for
	private static final int SPLASH_SCREEN_TIME = 2000;
	private static final int NUM_COLORS = 20;
		
	private VideoStatsFrame videoStatsFrame;
		
	private ExportTaskList exportTaskList;
	
	// Manages the available mark evaluators
	private MarkEvaluatorManager markEvaluatorManager;
	
	private OpenFile openFileCreator;
	
	private OpenFileTypeFactory openFileTypeFactory;
	
	private BoundIOContext context;
	
	public InteractiveBrowser(BoundIOContext context, ExportTaskList exportTaskList ) {
		super();
		this.exportTaskList = exportTaskList;
		this.context = context;
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
		
	public void showWithDefaultView() {
		videoStatsFrame.showWithDefaultView();
	}
	
	private void setup( InteractiveBrowserInput interactiveBrowserInput ) throws InitException {
		SubgrouppedAdder globalSubgroupAdder = new SubgrouppedAdder(videoStatsFrame,new DefaultModuleState());
		
		VideoStatsModuleGlobalParams moduleParams = createModuleParams(
			createExportPopupParams(),
			createColorIndex(),
			new RandomNumberGeneratorMersenne(false)
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
			context.getOutputManager()
		);
	}
	
	private void initMarkEvaluatorManager( InteractiveBrowserInput interactiveBrowserInput ) {
		markEvaluatorManager = new MarkEvaluatorManager(context);
		
		if (interactiveBrowserInput.getNamedItemMarkEvaluatorList()!=null) {
			for( NamedBean<MarkEvaluator> ni : interactiveBrowserInput.getNamedItemMarkEvaluatorList()) {
				markEvaluatorManager.add(
					ni.getName(),
					ni.getValue()
				);
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
			featureListSrc = interactiveBrowserInput.createFeatureListSrc(context.common());
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
			context.getLogger()
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
			importerSettings,
			videoStatsFrame.getLastMarkDisplaySettings()
		);
	}
	
	private void addGUIComponentsInner( AdderWithNrg adderNrg, FileCreatorLoader fileCreatorLoader, FileOpenManager fileOpenManager, List<FileCreator> fileCreators ) throws InitException {
		
		videoStatsFrame.getToolbar().add( openFileCreator);
		videoStatsFrame.getToolbar().addSeparator();
		
		videoStatsFrame.initBeforeAddingFrames( context.getErrorReporter() );
		
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
				context.getErrorReporter()
			)
		);
		
	}

	private ExportPopupParams createExportPopupParams() {
		SequenceMemory sequenceMemory = new SequenceMemory();
		ExportPopupParams popUpParams = new ExportPopupParams(
			context.getErrorReporter()
		);
		assert( context.getOutputManager()!= null );
		popUpParams.setOutputManager( context.getOutputManager() );
		popUpParams.setParentFrame( videoStatsFrame );
		popUpParams.setSequenceMemory( sequenceMemory );
		return popUpParams;
	}

	private VideoStatsModuleGlobalParams createModuleParams( ExportPopupParams popUpParams, ColorIndex colorIndex, RandomNumberGenerator re ) {
		return new VideoStatsModuleGlobalParams(
			popUpParams,
			context.common(),
			videoStatsFrame.getThreadPool(),
			re,
			exportTaskList,
			colorIndex,
			videoStatsFrame.getGraphicsConfiguration()
		);
	}
	
	private ColorIndex createColorIndex() throws InitException {
		try {
			return new HashedColorSet( new ShuffleColorSetGenerator( new HSBColorSetGenerator() ), NUM_COLORS );
		} catch (OperationFailedException e) {
			throw new InitException(e);
		}
	}

	private void displaySplashScreen() {
		// Display splash scren
		new SplashScreenTime(
			"/appSplash/anchor_splash.png",
			videoStatsFrame,
			SPLASH_SCREEN_TIME,
			context.getErrorReporter()
		);
	}
}
