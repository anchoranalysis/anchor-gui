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


import java.util.Iterator;
import java.util.Optional;

import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.BoundedIndexContainerIterator;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.gui.plot.visualvm.InternalFrameGraphAsModule;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class GraphCSVStatisticModuleCreator extends VideoStatsModuleCreatorContext {

	private final GraphDefinition<CSVStatistic> definition;
	private final FinderCSVStats finderCSVStats;
	
	public GraphCSVStatisticModuleCreator(
			GraphDefinition<CSVStatistic> definition,
			FinderCSVStats finderCSVStats) {
		super();
		this.definition = definition;
		this.finderCSVStats = finderCSVStats;
	}
	
	@Override
	public boolean precondition() {
		return finderCSVStats.exists();
	}

	@Override
	public Optional<IModuleCreatorDefaultState> moduleCreator(DefaultModuleStateManager defaultStateManager, String namePrefix,
			VideoStatsModuleGlobalParams mpg) throws VideoStatsModuleCreateException {

		try {
			final IBoundedIndexContainer<CSVStatistic> cntr = finderCSVStats.get();
			
			// We do a check that all necessary statistics are contained in container samples
			//  by taking the first item
			int minIndex = cntr.previousEqualIndex( cntr.getMaximumIndex() );
			CSVStatistic stat = cntr.get(minIndex);
			
			if (!definition.isItemAccepted(stat) ) {
				return Optional.empty();
			}
			
			Iterator<CSVStatistic> itr = new BoundedIndexContainerIterator<>(cntr, 1000);
			
			ClickableGraphInstance graphInstance = ClickableGraphFactory.create( definition, itr, null, null);
			
			String graphFrameTitle = new FrameTitleGenerator().genFramePrefix( namePrefix, title() );
			
			InternalFrameGraphAsModule frame = new InternalFrameGraphAsModule( graphFrameTitle, graphInstance );
			return Optional.of(
				frame.moduleCreator()
			);

		} catch (GetOperationFailedException | CreateException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}
		
	@Override
	public String title() {
		return definition.getTitle();
	}

	@Override
	public Optional<String> shortTitle() {
		return Optional.of(
			definition.getShortTitle()
		);
	}
}
