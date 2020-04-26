package org.anchoranalysis.gui.videostats.dropdown.multicollection;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;

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

import org.anchoranalysis.core.cache.IdentityOperation;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
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
import org.anchoranalysis.image.objmask.ObjMaskCollection;
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
	private INamedProvider<Cfg> cfgCollection;
	private INamedProvider<ObjMaskCollection> objCollection;
	private NamedProviderStore<KeyValueParams> paramsCollection;
	private boolean addProposerEvaluator;
	
	// A dropdown menu representing a particular manifest
	public MultiCollectionDropDown(
		OperationWithProgressReporter<TimeSequenceProvider,CreateException> rasterProvider,
		INamedProvider<Cfg> cfgCollection,
		INamedProvider<ObjMaskCollection> objCollection,
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
