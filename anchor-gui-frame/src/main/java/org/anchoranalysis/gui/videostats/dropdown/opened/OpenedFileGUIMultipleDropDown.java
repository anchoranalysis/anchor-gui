package org.anchoranalysis.gui.videostats.dropdown.opened;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JPopupMenu;

import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.frame.multioverlay.RasterMultiCreator;
import org.anchoranalysis.gui.frame.multiraster.NamedRasterSet;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.DualMenuWrapper;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;
import org.anchoranalysis.gui.videostats.modulecreator.RasterMultiModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperation;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationOrMenu;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationSequence;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

public class OpenedFileGUIMultipleDropDown {

	private JPopupMenu popupMenu = new JPopupMenu();
	private IAddVideoStatsModule adder;
	private VideoStatsModuleGlobalParams mpg;
	private MarkDisplaySettings markDisplaySettings;
	
	public OpenedFileGUIMultipleDropDown(
		IAddVideoStatsModule adder,
		List<IOpenedFileGUI> listOpenedGUI,
		VideoStatsModuleGlobalParams moduleParamsGlobal,
		MarkDisplaySettings markDisplaySettings
	) {
		
		if (listOpenedGUI.size()==0) {
			return;
		}
		
		this.adder = adder;
		this.mpg = moduleParamsGlobal;
		this.markDisplaySettings = markDisplaySettings;
		
		//@SuppressWarnings("unused")
		//ManifestDropDown first = listManifestDropDown.get(0);
		
		List<VideoStatsOperationMenu> firstList = new ArrayList<>(); 
		for( IOpenedFileGUI mdd : listOpenedGUI ) {
			firstList.add(mdd.getRootMenu());
		}
		
		VideoStatsOperationMenu out = new VideoStatsOperationMenu( new DualMenuWrapper(popupMenu) );
		
		VideoStatsOperationMenu individually = out.createSubMenu("Individually", false);
		VideoStatsOperationMenu combined = out.createSubMenu("Combined", false);
		
		traverse(
			firstList,
			individually,
			combined,
			markDisplaySettings
		);


	}
	
	private void traverse(
		List<VideoStatsOperationMenu> listIn,
		VideoStatsOperationMenu outIndividually,
		VideoStatsOperationMenu outCombined,
		MarkDisplaySettings markDisplaySettings
	) {
		
		VideoStatsOperationMenu first = listIn.get(0);
		
		for(VideoStatsOperationOrMenu vsoom : first.getListOperations()) {
			if (vsoom.isSeparator()) {
				outIndividually.addSeparator();
			} else if (vsoom.isOperation()) {
				addOperation(
					vsoom.getOperation(),
					listIn,
					outIndividually,
					outCombined,
					markDisplaySettings
				);
			} else if (vsoom.isMenu()) {
				addMenu(vsoom.getMenu(), listIn, outIndividually, outCombined);
			} else {
				assert false;
			}
		}
		
	}
	
	private void addOperation(
		VideoStatsOperation or,
		List<VideoStatsOperationMenu> list,
		VideoStatsOperationMenu outIndividually,
		VideoStatsOperationMenu outCombined,
		MarkDisplaySettings markDisplaySettings
	) {	
		VideoStatsOperationSequence all = new VideoStatsOperationSequence( or.getName() );
		all.add(or);
		
		List<IVideoStatsOperationCombine> listCombined = new ArrayList<>();
		if (or.getCombiner().isPresent()) {
			listCombined.add(
				or.getCombiner().get()
			);
		}
		
		// We loop through all the other menus, looking to see if they have the same item. We accept an item if they
		//  all have it.  We skip the first one, as it's not necessary.
		for( int i=1; i<list.size(); i++) {
			VideoStatsOperation sameNameItem = findOperationOfSameNameOrNull(or, list.get(i));
			if (sameNameItem==null) {
				return;
			}
			all.add(sameNameItem);
			
			if (sameNameItem.getCombiner().isPresent()) {
				listCombined.add(sameNameItem.getCombiner().get());
			}
		}
		outIndividually.add( all );
		
		if (listCombined.size()>0) {
			addCombinedOperations(
				listCombined,
				outCombined,
				or,
				markDisplaySettings
			);
		}
	}
	
	
	// We can improve this logic, as it's not the most clear cut
	private void addCombinedOperations(
		List<IVideoStatsOperationCombine> listCombined,
		VideoStatsOperationMenu out,
		VideoStatsOperation rootOperation,
		MarkDisplaySettings markDisplaySettings
	) {
		addBackgroundSetAndNoObjs(listCombined, out);
		
		// Get cfg menu
		addMulti(
			listCombined,
			out,
			rootOperation,
			"Cfg",
			new MultiCfgInputToOverlay(markDisplaySettings	),
			op->op.getCfg()
		);
		addMulti(
			listCombined,
			out,
			rootOperation,
			"Objs",
			new MultiObjMaskCollectionInputToOverlay(),
			op->op.getObjMaskCollection()
		);
	}
	
	
	/**
	 * Creates a "Multi Raster" for the operations that give Rasters but no Cfg or Objs
	 * 
	 * @param listCombined
	 * @param out
	 */
	private void addBackgroundSetAndNoObjs( List<IVideoStatsOperationCombine> listCombined, VideoStatsOperationMenu out ) {
		
		List<NamedRasterSet> list = new ArrayList<>();
		for( IVideoStatsOperationCombine op : listCombined ) {
			if (op.getNrgBackground()!=null && !op.getCfg().isPresent() && !op.getObjMaskCollection().isPresent()) {
				list.add( new NamedRasterSet( op.generateName(), op.getNrgBackground().getBackgroundSet()) );
			}
		}
		
		if (!list.isEmpty()) {
			RasterMultiModuleCreator creator = new RasterMultiModuleCreator(list,"multi-raster",mpg);
			VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder( new IdentityOperationWithProgressReporter<>(adder.createChild()), creator );
			out.add( new VideoStatsOperationFromCreatorAndAdder("Multi Raster",creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );		
		}
	}
	
	

	/**
	 * 
	 * @param <InputType> input-type
	 * @param listCombined
	 * @param outMenu
	 * @param rootOperation
	 * @param subMenuName
	 * @param bridge
	 * @param getObjFromOperationCombine
	 */
	private <T> void addMulti(
		List<IVideoStatsOperationCombine> listCombined,
		VideoStatsOperationMenu outMenu,
		VideoStatsOperation rootOperation,
		String subMenuName,
		BridgeElementWithIndex<MultiInput<T>, OverlayedInstantState, OperationFailedException> bridge,
		GetObjFromOperationCombine<T> getObjFromOperationCombine
	) {
		
		// First we make a MultRaster, from all that suppport MultiRaster
		List<MultiInput<T>> list = new ArrayList<>();
		for( IVideoStatsOperationCombine op : listCombined ) {
			if (op.getNrgBackground()!=null && getObjFromOperationCombine.getObj(op).isPresent()) {
				
				Optional<Operation<T,OperationFailedException>> opt = getObjFromOperationCombine.getObj(op);
				if (opt.isPresent()) {
					MultiInput<T> multiInput = new MultiInput<>(
						op.generateName(),
						op.getNrgBackground(),
						opt.get()
					); 
					list.add( multiInput );
				}
			}
		}
		
		if (list.size()>0) {
			VideoStatsOperationMenu subMenu = outMenu.getOrCreateSubMenu(subMenuName, true);
			
			RasterMultiCreator<T> creator = new RasterMultiCreator<>(
				list,
				rootOperation.getName(),
				mpg,
				bridge
			);
			VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder( new IdentityOperationWithProgressReporter<>(adder), creator );
			subMenu.add(
				new VideoStatsOperationFromCreatorAndAdder(
					rootOperation.getName(),
					creatorAndAdder,
					mpg.getThreadPool(),
					mpg.getLogErrorReporter()
				)
			);		
		}
	}
	
	@FunctionalInterface
	private interface GetObjFromOperationCombine<T> {
	  public Optional<Operation<T,OperationFailedException>> getObj( IVideoStatsOperationCombine op );
	}
	
	
	
	private void addMenu( VideoStatsOperationMenu menu, List<VideoStatsOperationMenu> list, VideoStatsOperationMenu out, VideoStatsOperationMenu outCombined) {
		
		ArrayList<VideoStatsOperationMenu> menuIn = new ArrayList<>();
		menuIn.add(menu);
		
		// We loop through all the other menus, looking to see if they have the same item. We accept an item if they
		//  all have it.  We skip the first one, as it's not necessary.
		for( int i=1; i<list.size(); i++) {
			VideoStatsOperationMenu sameNameItem = findMenuOfSameNameOrNull(menu, list.get(i));
			if (sameNameItem==null) {
				return;
			}
			menuIn.add(sameNameItem);
		}
		
		VideoStatsOperationMenu menuNew = out.createSubMenu(menu.getName(), true);
		traverse(menuIn,menuNew,outCombined,markDisplaySettings);
	}
	
	private static VideoStatsOperation findOperationOfSameNameOrNull( VideoStatsOperation src, VideoStatsOperationMenu menu) {
		
		for(VideoStatsOperationOrMenu vsoom : menu.getListOperations()) {
			if (vsoom.isOperation()) {
				if (src.getName().equals(vsoom.getOperation().getName())) {
					return vsoom.getOperation();
				}
			}
		}
		return null;
	}
	
	private static VideoStatsOperationMenu findMenuOfSameNameOrNull( VideoStatsOperationMenu src, VideoStatsOperationMenu menu) {
		
		for(VideoStatsOperationOrMenu vsoom : menu.getListOperations()) {
			if (vsoom.isMenu()) {
				
				// As names can also be null
				if (src.getName()==null) {
					if (vsoom.getMenu().getName()==null) {
						return vsoom.getMenu();
					} else {
						continue;
					}
				}
				
				if (vsoom.getMenu().getName()==null) {
					continue;
				}
				
				if (src.getName().equals(vsoom.getMenu().getName())) {
					return vsoom.getMenu();
				}
			}
		}
		return null;
	}
	
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
	
	
}
