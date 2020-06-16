package org.anchoranalysis.gui.videostats.dropdown.multicollection;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.finder.OperationFindNrgStackFromStackCollection;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.GuessNRGStackFromStacks;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapTimeSequenceAsStack;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@SuppressWarnings("unused")
public class MultiCollectionDropDown {
	
	private BoundVideoStatsModuleDropDown delegate;

	private OperationWithProgressReporter<TimeSequenceProvider,CreateException> rasterProvider;
	private NamedProvider<Cfg> cfgCollection;
	private NamedProvider<ObjectMaskCollection> objCollection;
	private NamedProviderStore<KeyValueParams> paramsCollection;
	private boolean addProposerEvaluator;
	
	// A dropdown menu representing a particular manifest
	public MultiCollectionDropDown(
		OperationWithProgressReporter<TimeSequenceProvider,CreateException> rasterProvider,
		NamedProvider<Cfg> cfgCollection,
		NamedProvider<ObjectMaskCollection> objCollection,
		NamedProviderStore<KeyValueParams> paramsCollection,
		String name,
		boolean addProposerEvaluator
	) {
		this.rasterProvider = rasterProvider;
		this.cfgCollection = cfgCollection;
		this.paramsCollection = paramsCollection;
		this.delegate = new BoundVideoStatsModuleDropDown( name, "/toolbarIcon/rectangle.png");
		this.addProposerEvaluator = addProposerEvaluator;
		this.objCollection = objCollection;
	}
	
	public void init(
		final IAddVideoStatsModule adder,
		BoundOutputManagerRouteErrors outputManager,
		MarkCreatorParams params
	) throws InitException {
		
		OperationCreateBackgroundSetWithAdder operationBwsa = new OperationCreateBackgroundSetWithAdder(
			NRGBackground.createStackSequence(
				rasterProvider,
				new GuessNRGStackFromStacks(rasterProvider)
			),
			adder,
			params.getModuleParams().getThreadPool(),
			params.getModuleParams().getLogErrorReporter().getErrorReporter()
		);
		
		DropDownUtilitiesRaster.addRaster(
			delegate.getRootMenu(),
			delegate,
			operationBwsa.nrgBackground(),
			"Raster",
			params.getModuleParams(),
			true		// Adds as default operation
		);
		
		if (cfgCollection!=null) {
			DropDownUtilities.addCfgSubmenu(
				delegate.getRootMenu(),
				delegate,
				cfgCollection,
				operationBwsa.nrgBackground(),
				params.getModuleParams(),
				params.getMarkDisplaySettings(),
				false
			);
		}
		
		if (objCollection!=null) {
			DropDownUtilities.addObjSubmenu(
				delegate.getRootMenu(),
				delegate,
				objCollection,
				operationBwsa.nrgBackground(),
				params.getModuleParams(),
				false
			);
		}
		
		if (addProposerEvaluator) {
			addProposerEvaluator(
				params.getMarkEvaluatorManager(),
				operationBwsa,
				params.getModuleParams(),
				outputManager
			);
		}
	}
	
	private void addProposerEvaluator(
		MarkEvaluatorManager markEvaluatorManager,
		OperationCreateBackgroundSetWithAdder operationBwsa,
		VideoStatsModuleGlobalParams mpg,
		BoundOutputManagerRouteErrors outputManager
	) throws InitException {
		
		BoundOutputManagerRouteErrors outputManagerSub = DropDownUtilities.createOutputManagerForSubfolder( outputManager, delegate.getName() );
		
		try {
			MarkEvaluatorSetForImage markEvaluatorSet = markEvaluatorManager.createSetForStackCollection(
					progressReporter -> new WrapTimeSequenceAsStack(
						rasterProvider.doOperation(progressReporter).sequence()
					),
				() -> ParamsUtils.apply(
					paramsCollection,
					mpg.getLogErrorReporter().getErrorReporter()
				)
			);
			
			// If we have a markEvaluator, then we add some extra menus
			if (markEvaluatorSet.hasItems()) {
				
				// TODO make this quicker
				// We only need specific images for the MarkProposer not the entire background set
				DropDownUtilities.addAllProposerEvaluator(
					delegate,
					operationBwsa.operationAdder(),
					operationBwsa.nrgBackground().getNRGBackground().getBackgroundSet(),
					markEvaluatorSet,
					outputManagerSub.getOutputWriteSettings(),
					true,
					mpg
				);
			}
			
		} catch (CreateException e) {
			throw new InitException(e);
		}
	}

	public IOpenedFileGUI openedFileGUI() {
		return delegate.openedFileGUI();
	}

	public String getName() {
		return delegate.getName();
	}
}
