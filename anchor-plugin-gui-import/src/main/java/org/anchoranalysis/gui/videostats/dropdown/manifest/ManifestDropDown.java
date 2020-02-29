package org.anchoranalysis.gui.videostats.dropdown.manifest;

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


import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollectionCombine;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollectionFromFolder;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollectionFromNrgStack;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollectionFromRootFiles;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.io.loader.manifest.finder.CfgNRGFinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolderKernelIterDescription;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.GuessNRGStackFromStacks;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.NRGTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.KernelIterDescriptionModuleCreator;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGNonHandleInstantState;
import ch.ethz.biol.cell.mpp.nrg.CfgNRG;
import ch.ethz.biol.cell.mpp.nrg.CfgNRGPixelized;

public class ManifestDropDown {
	
	private BoundVideoStatsModuleDropDown delegate;

	private CoupledManifests manifests;
	
	private MarkDisplaySettings markDisplaySettings;
	
	// A dropdown menu representing a particular manifest
	public ManifestDropDown(CoupledManifests manifests, MarkDisplaySettings markDisplaySettings ) {
		this.manifests = manifests;
		this.markDisplaySettings = markDisplaySettings;
		this.delegate = new BoundVideoStatsModuleDropDown( manifests.descriptiveName(), "/toolbarIcon/rectangle.png");
	}

	private OperationCreateBackgroundSetWithAdder createBackgroundSetWithNRG(
		FinderNrgStack finderNrgStack,
		FinderImgStackCollectionCombine finderImgStackCollection,
		IAddVideoStatsModule adder,
		VideoStatsModuleGlobalParams mpg
	) {
		
		OperationWithProgressReporter<INamedProvider<Stack>> opStacks = finderImgStackCollection.getImgStackCollectionAsOperationWithProgressReporter();  
		
		// We try to read a nrgStack from the manifest. If none exists, we guess instead from the image-stacks.
		OperationWithProgressReporter<NRGStackWithParams> opNrg = new OperationReplaceNull<>(
			finderNrgStack.operationNrgStackWithProgressReporter(),
			new GuessNRGStackFromStacks(
				progressReporter -> new TimeSequenceProvider(
					new WrapStackAsTimeSequence(
						opStacks.doOperation(progressReporter)
					),
					1
				)
			)
		); 
		
		NRGBackground nrgBackground = NRGBackground.createStack(
			opStacks,
			opNrg
		);
		
		return new OperationCreateBackgroundSetWithAdder(
			nrgBackground,
			adder,
			mpg.getThreadPool(),
			mpg.getLogErrorReporter().getErrorReporter()
		);
	}
	
	private FinderImgStackCollectionCombine createFinderImgStack( FinderNrgStack finderNrgStack, RasterReader rasterReader ) throws InitException {
		// Finders
		FinderImgStackCollectionCombine finderImgStackCollection = new FinderImgStackCollectionCombine();
		finderImgStackCollection.add( new FinderImgStackCollectionFromFolder( rasterReader, "stackCollection" ) );
		finderImgStackCollection.add( new FinderImgStackCollectionFromRootFiles( rasterReader, "out" ) );
		finderImgStackCollection.add( new FinderImgStackCollectionFromNrgStack(finderNrgStack));		// Adds the NRG stack to the input stack collection
		
		// Disabled, as they not be the same size as the existing image
		finderImgStackCollection.add( new FinderImgStackCollectionFromRootFiles( rasterReader, "stackFromCollection" ) );
		finderImgStackCollection.add( new FinderImgStackCollectionFromFolder( rasterReader, "chnlScaledCollection" ) );
		
		try {
			if (!finderImgStackCollection.doFind(manifests.getFileManifest().doOperation())) {
				throw new InitException("cannot find image stack collection");
			}
		} catch (ExecuteException e1) {
			throw new InitException(e1);
		}
		
		return finderImgStackCollection;
	}
	
	private FinderNrgStack createFinderNrgStack( RasterReader rasterReader, VideoStatsModuleGlobalParams mpg ) throws InitException {
		FinderNrgStack finderNrgStack = new FinderNrgStack(	rasterReader, mpg.getLogErrorReporter().getErrorReporter() );
		try {
			finderNrgStack.doFind(manifests.getFileManifest().doOperation());
		} catch (ExecuteException e1) {
			throw new InitException(e1);
		}
		return finderNrgStack;
	}

	private FinderSerializedObject<KernelProposer<CfgNRGPixelized>> createFinderKernelProposer( VideoStatsModuleGlobalParams mpg ) {
		FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer = new FinderSerializedObject<>("kernelProposer", mpg.getLogErrorReporter().getErrorReporter() );
		finderKernelProposer.doFind(manifests.getExperimentManifest());
		return finderKernelProposer;
	}
	
	private FinderSerializedObject<KeyValueParams> createFinderGroupParams( VideoStatsModuleGlobalParams mpg ) throws InitException {
		final FinderSerializedObject<KeyValueParams> finderGroupParams =
				new FinderSerializedObject<>("groupParams", mpg.getLogErrorReporter().getErrorReporter() );
			try {
				finderGroupParams.doFind(manifests.getFileManifest().doOperation());
			} catch (ExecuteException e) {
				throw new InitException(e);
			}
		return finderGroupParams;
	}
	
	private FinderHistoryFolderKernelIterDescription createFinderKernelIterDescription( VideoStatsModuleGlobalParams mpg ) throws InitException {
		
		final FinderHistoryFolderKernelIterDescription finderKernelIterDescription = new FinderHistoryFolderKernelIterDescription("kernelIterDescription", mpg.getCacheMonitor());
		try {
			finderKernelIterDescription.doFind(manifests.getFileManifest().doOperation());
		} catch (ExecuteException e) {
			throw new InitException(e);
		}
		return finderKernelIterDescription;
	}
	

	private MarkEvaluatorSetForImage createMarkEvaluatorSet(
		MarkEvaluatorManager markEvaluatorManager,
		FinderImgStackCollectionCombine finderImgStackCollection,
		FinderSerializedObject<KeyValueParams> finderGroupParams
	) throws InitException {
		try {
			return markEvaluatorManager.createSetForStackCollection(
				finderImgStackCollection.getImgStackCollectionAsOperationWithProgressReporter(),
				finderGroupParams.operation()
			);
		} catch (CreateException e1) {
			throw new InitException(e1);
		}
	}
		
	public void init(
		IAddVideoStatsModule adder,
		RasterReader rasterReader,
		MarkEvaluatorManager markEvaluatorManager,
		BoundOutputManagerRouteErrors outputManager,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {

		// Some necessary Finders and Objects
		FinderNrgStack finderNrgStack = createFinderNrgStack( rasterReader, mpg );
		FinderImgStackCollectionCombine finderImgStackCollection = createFinderImgStack( finderNrgStack, rasterReader );
		OperationCreateBackgroundSetWithAdder opBackgroundNRG = createBackgroundSetWithNRG( finderNrgStack, finderImgStackCollection, adder, mpg );
		
		// Add: Cfgs, Rasters and ObjMasks
		boolean defaultAdded = addCfgs(	opBackgroundNRG, finderNrgStack, mpg );
		addRaster( opBackgroundNRG, mpg, defaultAdded );
		new AddObjs(
			delegate,
			manifests,
			finderNrgStack,
			mpg,
			markDisplaySettings
		).apply(opBackgroundNRG);

		// Various useful finders
		FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer = createFinderKernelProposer( mpg );

		FinderSerializedObject<KeyValueParams> finderGroupParams = createFinderGroupParams( mpg );
		FinderHistoryFolderKernelIterDescription finderKernelIterDescription = createFinderKernelIterDescription( mpg );

		addKernelHistoryNavigator( finderKernelIterDescription, finderKernelProposer, opBackgroundNRG, mpg );
				
		outputManager = DropDownUtilities.createOutputManagerForSubfolder( outputManager, delegate.getName() );

		// Proposer Evaluators
		MarkEvaluatorSetForImage markEvaluatorSet = createMarkEvaluatorSet( markEvaluatorManager, finderImgStackCollection, finderGroupParams );
		addProposerEvaluator( finderImgStackCollection, finderNrgStack, markEvaluatorSet, outputManager, adder, mpg );
		
		delegate.getRootMenu().addSeparator();
		
		addSetsWrap( finderNrgStack, finderImgStackCollection, opBackgroundNRG, finderKernelProposer, outputManager, adder, mpg );
	}
	
	private void addSetsWrap(
			FinderNrgStack finderNrgStack,
			FinderImgStackCollectionCombine finderImgStackCollection,
			OperationCreateBackgroundSetWithAdder opBackgroundNRG,
			FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
			BoundOutputManagerRouteErrors outputManager,
			IAddVideoStatsModule adder,
			VideoStatsModuleGlobalParams mpg
		) {
			try {
				CfgNRGFinderContext context = new CfgNRGFinderContext(
					finderImgStackCollection,
					finderKernelProposer,
					adder.getParentFrame(),
					outputManager,
					mpg					
				);
				
				AddSets addSets = new AddSets(
					delegate,
					opBackgroundNRG,
					context
				);
				
				addSets.apply(manifests,finderNrgStack);
				
			} catch (OperationFailedException e) {
				mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
			}
		}
	
	
	
	private void addProposerEvaluator(
			FinderImgStackCollectionCombine finderImgStackCollection,
			FinderNrgStack finderNrgStack,
			MarkEvaluatorSetForImage markEvaluatorSet,
			BoundOutputManagerRouteErrors outputManager,
			IAddVideoStatsModule adder,
			VideoStatsModuleGlobalParams mpg
		) {
		OperationCreateBackgroundSetWithAdder operationBwsa = new OperationCreateBackgroundSetWithAdder(
			NRGBackground.createStack(
				finderImgStackCollection.getImgStackCollectionAsOperationWithProgressReporter(),
				finderNrgStack.operationNrgStackWithProgressReporter()
			),
			adder,
			mpg.getThreadPool(),
			mpg.getLogErrorReporter().getErrorReporter()
		);
		
		// TODO make this quicker
		// We only need specific images for the MarkProposer not the entire background set
		DropDownUtilities.addAllProposerEvaluator(
			delegate,
			operationBwsa.operationAdder(),
			operationBwsa.nrgBackground().getNRGBackground().getBackgroundSet(),
			markEvaluatorSet,
			outputManager.getOutputWriteSettings(),
			true,
			mpg
		);
	}
	
	
	private void addKernelHistoryNavigator(
		FinderHistoryFolderKernelIterDescription finderKernelIterDescription,
		FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
		OperationCreateBackgroundSetWithAdder opBackgroundNRG,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		if (finderKernelIterDescription.exists() && finderKernelProposer.exists() ) {
			try {
				delegate.addModule(
					opBackgroundNRG.operationAdder(),
					new SingleContextualModuleCreator(
						new KernelIterDescriptionModuleCreator( finderKernelIterDescription, finderKernelProposer)
					),
					"Kernel History Navigator",
					mpg
				);
			} catch (MenuAddException e) {
				throw new InitException(e);
			}
		}
	}
	
	
	
	
	private void addRaster( OperationCreateBackgroundSetWithAdder opBackground, VideoStatsModuleGlobalParams mpg, boolean defaultAdded ) {
		DropDownUtilitiesRaster.addRaster(
			delegate.getRootMenu(),
			delegate,
			opBackground.nrgBackground(),
			"Raster",
			mpg,
			!defaultAdded		// Adds as default operation
		);	
	}
	
	
	
	// Returns TRUE if it's added a default, FALSE otherwise
	private boolean addCfgs( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG, FinderNrgStack finderNrgStack, VideoStatsModuleGlobalParams mpg ) {
		
		boolean defaultAdded = false;
		
		try	{
			final FinderSerializedObject<Cfg> finderFinalCfg = new FinderSerializedObject<>("cfg", mpg.getLogErrorReporter().getErrorReporter() );
			finderFinalCfg.doFind(manifests.getFileManifest().doOperation());
			
			if (finderFinalCfg.exists()) {
				
				DropDownUtilities.addCfg(
					delegate.getRootMenu(),
					delegate,
					finderFinalCfg.operation(),
					"Final Cfg",
					operationBwsaWithNRG.nrgBackground(),
					mpg,
					markDisplaySettings,
					true
				);
				defaultAdded = true;
			}
		} catch (ExecuteException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}

		
		if (addNRGTable(operationBwsaWithNRG, finderNrgStack, mpg)) {
			defaultAdded = true;
		}
				
		addCfgSubMenu( operationBwsaWithNRG, finderNrgStack, mpg );
		return defaultAdded;
	}
	
	
	
	private boolean addNRGTable( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG, FinderNrgStack finderNrgStack, VideoStatsModuleGlobalParams mpg ) {
		try	{
			final FinderSerializedObject<CfgNRG> finderFinalCfgNRG = new FinderSerializedObject<>("cfgNRG", mpg.getLogErrorReporter().getErrorReporter() );
			finderFinalCfgNRG.doFind(manifests.getFileManifest().doOperation());
			
			if (finderFinalCfgNRG.exists()) {
				
				CachedOperation<LoadContainer<CfgNRGInstantState>> op = new CachedOperation<LoadContainer<CfgNRGInstantState>>() {

					@Override
					protected LoadContainer<CfgNRGInstantState> execute()
							throws ExecuteException {
						
						CfgNRGInstantState instantState;
						try {
							instantState = new CfgNRGNonHandleInstantState(0, finderFinalCfgNRG.get() );
						} catch (GetOperationFailedException e) {
							throw new ExecuteException(e);
						}
						
						LoadContainer<CfgNRGInstantState> lc = new LoadContainer<CfgNRGInstantState>();
						lc.setExpensiveLoad(false);
						lc.setCntr( new SingleContainer<CfgNRGInstantState>(instantState, 0, false));
						return lc;
					}
				};
				
				if (finderNrgStack.exists()) {
					// NRG Table
					SingleContextualModuleCreator creator = new SingleContextualModuleCreator(
						new NRGTableCreator(
							op,
							finderNrgStack.operationNrgStack(),
							mpg.getDefaultColorIndexForMarks()
						)
					);
					delegate.addModule(
						operationBwsaWithNRG.operationAdder(),
						creator,
						"NRG Table",
						mpg
					);
				}
								
				return true;
			}
		} catch (ExecuteException | MenuAddException e) {
			 mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}
		 return false;
	}
	
	
	private void addCfgSubMenu( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG, FinderNrgStack finderNrgStack, VideoStatsModuleGlobalParams mpg ) {
		try {
			FinderCfgFolder finder = new FinderCfgFolder("cfgCollection", "cfg");
			finder.doFind(manifests.getFileManifest().doOperation());
			
			INamedProvider<Cfg> provider = finder.createNamedProvider(false, mpg.getLogErrorReporter());
			DropDownUtilities.addCfgSubmenu(
				delegate.getRootMenu(),
				delegate,
				provider,
				operationBwsaWithNRG.nrgBackground(),
				mpg,
				markDisplaySettings,
				false
			);
			
		} catch (ExecuteException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		} catch (OperationFailedException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}
		
		
	}
	
	public IOpenedFileGUI openedFileGUI() {
		return delegate.openedFileGUI();
	}

	public String getName() {
		return delegate.getName();
	}
}
