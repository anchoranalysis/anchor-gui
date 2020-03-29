package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

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

import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.graph.creator.GraphFromDualFinderCreator;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

public class GraphDualFinderCreator<ItemType> extends ContextualModuleCreator {

	private final GraphFromDualFinderCreator<ItemType> creator;
	private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
	private final FinderCSVStats finderCSVStats;
	private final GraphColorScheme graphColorScheme;

	public GraphDualFinderCreator(GraphFromDualFinderCreator<ItemType> creator,
			FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
			FinderCSVStats finderCSVStats, GraphColorScheme graphColorScheme) {
		super();
		this.creator = creator;
		this.finderCfgNRGHistory = finderCfgNRGHistory;
		this.finderCSVStats = finderCSVStats;
		this.graphColorScheme = graphColorScheme;
	}

	@Override
	public NamedModule[] create(String namePrefix, OperationWithProgressReporter<IAddVideoStatsModule> adder, VideoStatsModuleGlobalParams mpg )
			throws CreateException {
		
		ArrayList<NamedModule> outList = new ArrayList<>();
		
		if (!finderCfgNRGHistory.exists() && !finderCSVStats.exists()) {
			return new NamedModule[] {};
		}

		VideoStatsModuleCreator preferredCreator = null;

		GraphDefinition<ItemType> definition = creator.createGraphDefinition( graphColorScheme );
		
		// Priority given to CSV stats if available
		if (finderCSVStats.exists()) {
			preferredCreator = creator.createGraphModule( namePrefix, definition, finderCfgNRGHistory, finderCSVStats,true);
			VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder( adder, preferredCreator );
			outList.add(
				new NamedModule( definition.getTitle(), creatorAndAdder, definition.getShortTitle() )
			);
		} 
		
		// Let's make another
		if (finderCfgNRGHistory.exists()) {
			VideoStatsModuleCreator moduleCreator = creator.createGraphModule(namePrefix,definition,finderCfgNRGHistory, finderCSVStats, false ); 
			VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder( adder, moduleCreator );
			outList.add(
				new NamedModule(
					String.format("%s (Instance CfgNRG Data)",definition.getTitle()),
					creatorAndAdder,
					definition.getShortTitle()
				)
			);
		}
		
		return outList.toArray( new NamedModule[]{} );
	}
}
