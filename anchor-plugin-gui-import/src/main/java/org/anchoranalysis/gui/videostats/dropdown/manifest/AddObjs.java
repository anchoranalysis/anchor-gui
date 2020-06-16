package org.anchoranalysis.gui.videostats.dropdown.manifest;

import java.io.IOException;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderObjMaskCollectionFolder;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilities;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.NRGTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

class AddObjs {

	private BoundVideoStatsModuleDropDown delegate;
	private CoupledManifests manifests;
	private FinderNrgStack finderNrgStack;
	private VideoStatsModuleGlobalParams mpg;
	private MarkDisplaySettings markDisplaySettings;
		
	public AddObjs(BoundVideoStatsModuleDropDown delegate, CoupledManifests manifests, FinderNrgStack finderNrgStack,
			VideoStatsModuleGlobalParams mpg, MarkDisplaySettings markDisplaySettings) {
		super();
		this.delegate = delegate;
		this.manifests = manifests;
		this.finderNrgStack = finderNrgStack;
		this.mpg = mpg;
		this.markDisplaySettings = markDisplaySettings;
	}
	
	public boolean apply( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG ) {
		
		VideoStatsOperationMenu subMenu = delegate.getRootMenu().createSubMenu("Objs", true);
		
		boolean defaultAdded = false;
		
		if (fromObjMaskCollectionFolder(subMenu, operationBwsaWithNRG)) {
			defaultAdded = true;
		}
		
		if (fromSerializedCfgNRG(operationBwsaWithNRG)) {
			defaultAdded = true;
		}
		
		fromCfg(operationBwsaWithNRG);
		
		return defaultAdded;
	}

	private boolean fromObjMaskCollectionFolder(
		VideoStatsOperationMenu subMenu,
		OperationCreateBackgroundSetWithAdder operationBwsaWithNRG
	) {
		try	{
			final FinderObjMaskCollectionFolder finderObjs = new FinderObjMaskCollectionFolder("objMaskCollection" );
			finderObjs.doFind(manifests.getFileManifest().doOperation());
			
			if (finderObjs.exists()) {
				
				NamedProvider<ObjectCollection> providers = finderObjs.createNamedProvider(false, mpg.getLogErrorReporter());
				
				for( String key : providers.keys() ) {
					DropDownUtilities.addObjMaskCollection(
						subMenu,
						delegate,
						new OperationFromNamedProvider<ObjectCollection>(providers,key),
						key,
						operationBwsaWithNRG.nrgBackground(),
						mpg,
						true
					);
				}
				return true;
			} 
		
		} catch (OperationFailedException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}
		
		return false;
	
	}
	
	private boolean fromSerializedCfgNRG( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG ) {
		try	{
			final FinderSerializedObject<CfgNRG> finderFinalCfgNRG = new FinderSerializedObject<>("cfgNRG", mpg.getLogErrorReporter().getErrorReporter() );
			finderFinalCfgNRG.doFind(manifests.getFileManifest().doOperation());
			
			if (finderFinalCfgNRG.exists()) {
				
				CachedOperation<LoadContainer<CfgNRGInstantState>,GetOperationFailedException> op =
					new WrapOperationAsCached<>(
						() -> {
							CfgNRGInstantState instantState;
							try {
								instantState = new CfgNRGNonHandleInstantState(0, finderFinalCfgNRG.get() );
							} catch (IOException e) {
								throw new GetOperationFailedException(e);
							}
							
							LoadContainer<CfgNRGInstantState> lc = new LoadContainer<CfgNRGInstantState>();
							lc.setExpensiveLoad(false);
							lc.setCntr( new SingleContainer<CfgNRGInstantState>(instantState, 0, false));
							return lc;
						}
					);
				
				if (finderNrgStack.exists()) {
					// NRG Table
					SingleContextualModuleCreator creator = new SingleContextualModuleCreator( 
						new NRGTableCreator(
							op,
							finderNrgStack.operationNrgStack(),
							mpg.getDefaultColorIndexForMarks()
						)
					);
					delegate.addModule( operationBwsaWithNRG.operationAdder(), creator, "NRG Table", mpg );
				}
								
				return true;
			}
		} catch (MenuAddException | OperationFailedException e) {
			 mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}
		return false;
	}
	
	private void fromCfg( OperationCreateBackgroundSetWithAdder operationBwsaWithNRG ) {
		
		try {
			FinderCfgFolder finder = new FinderCfgFolder("cfgCollection", "cfg");
			finder.doFind(manifests.getFileManifest().doOperation());
			
			NamedProvider<Cfg> provider = finder.createNamedProvider(false, mpg.getLogErrorReporter());
			DropDownUtilities.addCfgSubmenu(
				delegate.getRootMenu(),
				delegate,
				provider,
				operationBwsaWithNRG.nrgBackground(),
				mpg,
				markDisplaySettings,
				false
			);
			
		} catch (OperationFailedException e) {
			mpg.getLogErrorReporter().getErrorReporter().recordError(ManifestDropDown.class, e);
		}
		
	}
}
