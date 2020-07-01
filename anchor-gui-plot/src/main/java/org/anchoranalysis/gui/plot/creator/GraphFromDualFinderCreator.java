package org.anchoranalysis.gui.plot.creator;

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


import java.util.Iterator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.BoundedIndexContainerIterator;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.visualvm.InternalFrameGraphAsModule;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

public abstract class GraphFromDualFinderCreator<ItemType> {
	
	public abstract IBoundedIndexContainer<ItemType> createCntr( final FinderCSVStats finderCSVStats ) throws CreateException;
	public abstract IBoundedIndexContainer<ItemType> createCntr( final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory ) throws CreateException;
	
	public abstract GraphDefinition<ItemType> createGraphDefinition( GraphColorScheme graphColorScheme ) throws CreateException;
	
	// useCSV is a flag indicating which of the two to use
	public VideoStatsModuleCreator createGraphModule( final String windowTitlePrefix, final GraphDefinition<ItemType> definition, final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory, final FinderCSVStats finderCSVStats, final boolean useCSV ) {
		return new VideoStatsModuleCreator() {
			
			@Override
			public void createAndAddVideoStatsModule(IAddVideoStatsModule adder) throws VideoStatsModuleCreateException {

				try {
					// We calculate our container
					IBoundedIndexContainer<ItemType> cntr;
					if (useCSV && finderCSVStats.exists()) {
						cntr = createCntr(finderCSVStats);
					} else if (finderCfgNRGHistory.exists()) {
						cntr = createCntr(finderCfgNRGHistory);
					} else {
						return;
					}
					
					Iterator<ItemType> itr = new BoundedIndexContainerIterator<>(cntr, 1000);
					
					String graphFrameTitle = new FrameTitleGenerator().genFramePrefix( windowTitlePrefix, definition.getTitle() );

					InternalFrameGraphAsModule frame = new InternalFrameGraphAsModule(
						graphFrameTitle,
						ClickableGraphFactory.create(definition, itr, null, null)
					);
					
					ModuleAddUtilities.add( adder, frame.moduleCreator() );

				} catch (CreateException e) {
					throw new VideoStatsModuleCreateException(e);
				}
			}
		};
	}
}