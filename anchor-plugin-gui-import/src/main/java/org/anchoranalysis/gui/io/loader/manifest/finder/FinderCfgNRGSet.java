/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.io.loader.manifest.finder;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolderCfgNRGInstantState;
import org.anchoranalysis.gui.videostats.dropdown.CfgNRGHistoryMenu;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.Finder;

public class FinderCfgNRGSet implements Finder, ContainerGetter<CfgNRGInstantState> {
	
	// Constructor fields
	private String setName;
	private String manifestNameCfgNRGHistory;
	
	private String csvStatsName;
	private FinderContext context;
	
	
	// Return
	private CfgNRGHistoryMenu dropDown;
	private FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
	
	private FinderCfgNRGSet secondary;
	private FinderCfgNRGSet tertiary;
	
	public FinderCfgNRGSet(
		String setName,
		String csvStatsName,
		String manifestNameCfgNRGHistory,
		FinderContext context,
		FinderCfgNRGSet secondary,
		FinderCfgNRGSet tertiary
	) {
		super();
		this.setName = setName;
		this.manifestNameCfgNRGHistory = manifestNameCfgNRGHistory;
		this.csvStatsName = csvStatsName;
		this.context = context;
		this.secondary = secondary;
		this.tertiary = tertiary;
	}

	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {

		if (context.getMpg().getDefaultColorIndexForMarks()==null) {
			return false;
		}
		
		// Configurations with NRG
		finderCfgNRGHistory = new FinderHistoryFolderCfgNRGInstantState(
			manifestNameCfgNRGHistory
		);
		finderCfgNRGHistory.doFind(manifestRecorder);

		// For now we treat both these as obligatory
		if (!finderCfgNRGHistory.exists()) {
			return false;
		}
		
		// CSV Stats
		final FinderCSVStats finderCSVStats = new FinderCSVStats(
			csvStatsName,
			context.getMpg().getLogger().errorReporter()
		);
		finderCSVStats.doFind(manifestRecorder);
		
		//if (!finderCSVStats.exists()) {
		//	return false;
		//}
		
		dropDown = new CfgNRGHistoryMenu(
			context.getParentMenu(),
			setName,
			context.getBoundVideoStats().createChild(setName).createAddModuleToMenu(
				context.getNrgBackground().getAdder()
			)
		);
		try {
			dropDown.init(
				finderCfgNRGHistory,
				secondary,
				tertiary,
				context.getNrgBackground().getNRGBackground(),
				finderCSVStats,
				context.getContext()
			);
		} catch (InitException | MenuAddException e) {
			context.getMpg().getLogger().errorReporter().recordError(FinderCfgNRGSet.class, e);
			return false;
		}
		return true;
	}

	public CfgNRGHistoryMenu get() {
		return dropDown;
	}
	

	public LoadContainer<CfgNRGInstantState> getHistory() throws GetOperationFailedException {
		return finderCfgNRGHistory.get();
	}

	public String getSetName() {
		return setName;
	}
	
	public void createDefaultModules() {

		if (dropDown.getCreatorColoredOutline()!=null) {
			dropDown.getCreatorColoredOutline().createVideoStatsModuleForAdder(
				context.getMpg().getThreadPool(),
				context.getParentFrame(),
				context.getMpg().getLogger()
			);
		}
	}

	@Override
	public boolean exists() {
		return dropDown!=null;
	}

	@Override
	public BoundedIndexContainer<CfgNRGInstantState> getCntr() throws GetOperationFailedException {
		return getHistory().getCntr();
	}

}
