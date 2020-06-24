package org.anchoranalysis.gui.videostats.dropdown.common;



import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationNRGStackFromMarkEvaluatorSet;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.CfgModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.ObjMaskCollectionModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class DropDownUtilities {
	
	public static void addAllProposerEvaluator(
		BoundVideoStatsModuleDropDown dropDown,
		OperationWithProgressReporter<IAddVideoStatsModule,? extends Throwable> adderOpWithoutNRG,
		OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> backgroundSet,
		MarkEvaluatorSetForImage markEvaluatorSet,
		OutputWriteSettings outputWriteSettings,
		boolean addNRGAdder,
		VideoStatsModuleGlobalParams mpg
	) {
		// Generating a NRGStackWithParams from the markEvaluatorSet
		OperationNRGStackFromMarkEvaluatorSet operationGetNRGStack = new OperationNRGStackFromMarkEvaluatorSet(markEvaluatorSet);
		
		NRGBackground nrgBackground = NRGBackground.createFromBackground(
			backgroundSet,
			operationGetNRGStack
		);
		
		CachedOperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adderOp =
			new WrapOperationWithProgressReporterAsCached<>(
				pr -> {
					IAddVideoStatsModule adder = adderOpWithoutNRG.doOperation(pr);
					
					if (addNRGAdder) {
						nrgBackground.addNrgStackToAdder(adder);
					}
					return adder;
				}
			);
		
		VideoStatsModuleCreator moduleCreator = new ProposerEvaluatorModuleCreator(
			markEvaluatorSet,
			nrgBackground,
			outputWriteSettings,
			operationGetNRGStack,
			mpg
		); 	
		
		VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder(adderOp,moduleCreator); 
		dropDown.getRootMenu().add( new VideoStatsOperationFromCreatorAndAdder("Proposer Evaluator", creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );
	}
	
	public static void addCfg(
		VideoStatsOperationMenu menu,
		BoundVideoStatsModuleDropDown delegate,
		Operation<Cfg,OperationFailedException> op,
		String name,
		NRGBackgroundAdder<?> nrgBackground,
		VideoStatsModuleGlobalParams mpg,
		MarkDisplaySettings markDisplaySettings,
		boolean addAsDefault
	) {
		
		VideoStatsModuleCreator module = new CfgModuleCreator(
			delegate.getName(),
			name,
			op,
			nrgBackground.getNRGBackground(),
			mpg,
			markDisplaySettings
		);
		addModule(
			module,
			menu,
			nrgBackground.getAdder(),
			name,
			mpg,
			addAsDefault
		);
	}
	
	
	public static void addObjMaskCollection(
		VideoStatsOperationMenu menu,
		BoundVideoStatsModuleDropDown delegate,
		Operation<ObjectCollection,OperationFailedException> op,
		String name,
		NRGBackgroundAdder<?> nrgBackground,
		VideoStatsModuleGlobalParams mpg,
		boolean addAsDefault
	) {
		VideoStatsModuleCreator module = new ObjMaskCollectionModuleCreator(
			delegate.getName(),
			name,
			op,
			nrgBackground.getNRGBackground(),
			mpg
		);
		addModule(
			module,
			menu,
			nrgBackground.getAdder(),
			name,
			mpg,
			addAsDefault
		);
	}
	
	public static void addCfgSubmenu(
		VideoStatsOperationMenu menu,
		BoundVideoStatsModuleDropDown delegate,
		NamedProvider<Cfg> cfgProvider,
		NRGBackgroundAdder<?> nrgBackground,
		VideoStatsModuleGlobalParams mpg,
		MarkDisplaySettings markDisplaySettings,
		boolean addAsDefault
	) {
		if (cfgProvider.keys().size()==0) {
			return;
		}
		
		VideoStatsOperationMenu subMenu = menu.createSubMenu("Cfg", true);
		
		for( String providerName : cfgProvider.keys() ) {
			
			addCfg(
				subMenu,
				delegate.createChild(providerName),
				() -> getFromProvider(cfgProvider, providerName),
				providerName,
				nrgBackground,
				mpg,
				markDisplaySettings,
				addAsDefault
			);
		}
	}
		
	public static void addObjSubmenu(
		VideoStatsOperationMenu menu,
		BoundVideoStatsModuleDropDown delegate,
		final NamedProvider<ObjectCollection> provider,
		NRGBackgroundAdder<?> nrgBackground,
		VideoStatsModuleGlobalParams mpg,
		boolean addAsDefault
	) {
		if (provider.keys().size()==0) {
			return;
		}
		
		VideoStatsOperationMenu subMenu = menu.createSubMenu("Objs", true);
		
		for( final String providerName : provider.keys() ) {
			
			addObjMaskCollection(
				subMenu,
				delegate.createChild(providerName),
				() -> getFromProvider(provider,providerName),
				providerName,
				nrgBackground,
				mpg,
				addAsDefault
			);
		}
	}
	
	public static BoundOutputManagerRouteErrors createOutputManagerForSubfolder( BoundOutputManagerRouteErrors parentOutputManager, String subFolderName ) throws InitException {
		ManifestDescription manifestDescription = new ManifestDescription("interactiveOutput", "manifestInteractiveOutput");
		
		ManifestFolderDescription mfd = new ManifestFolderDescription();
    	mfd.setFileDescription( manifestDescription );
    	
    	// NB: As bindAsSubFolder can now return nulls, maybe some knock-on bugs are introduced here
		return parentOutputManager.getWriterAlwaysAllowed().bindAsSubFolder(subFolderName, mfd, Optional.empty()).orElseThrow( ()->
			new InitException("Cannot create a sub-folder for output")
		);
	}
	
	private static void addModule(
		VideoStatsModuleCreator module,
		VideoStatsOperationMenu menu,
		OperationWithProgressReporter<IAddVideoStatsModule,? extends Throwable> opAdder,
		String name,
		VideoStatsModuleGlobalParams mpg,
		boolean addAsDefault
	) {
		VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder(opAdder,module);
		
		if (addAsDefault) {
			menu.add( new VideoStatsOperationFromCreatorAndAdder(name,creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );
		} else {
			menu.addAsDefault( new VideoStatsOperationFromCreatorAndAdder(name,creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );
		}
	}
	
	private static <T> T getFromProvider(NamedProvider<T> provider, String name) throws OperationFailedException {
		try {
			return provider.getException(name);
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
}
