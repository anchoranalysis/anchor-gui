package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.plot.NRGGraphItem;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;

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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.cfgnrg.CfgNRGInstantStateGraphPanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrameHistoryCfgNRGInstantState;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.creator.GenerateGraphNRGBreakdownFromInstantState;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class GraphNRGBreakdownModuleCreator extends VideoStatsModuleCreatorContext {

	private final GraphDefinition<NRGGraphItem> definition;
	private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
	private final ColorIndex colorIndex;
	
	public GraphNRGBreakdownModuleCreator(GraphDefinition<NRGGraphItem> definition,
			FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory, ColorIndex colorIndex) {
		super();
		this.definition = definition;
		this.finderCfgNRGHistory = finderCfgNRGHistory;
		this.colorIndex = colorIndex;
	}

	@Override
	public boolean precondition() {
		return finderCfgNRGHistory.exists();
	}

	@Override
	public IModuleCreatorDefaultState moduleCreator(DefaultModuleStateManager defaultStateManager, String namePrefix,
			VideoStatsModuleGlobalParams mpg) throws VideoStatsModuleCreateException {

		ErrorReporter errorReporter = mpg.getLogErrorReporter().getErrorReporter();
		GenerateGraphNRGBreakdownFromInstantState generator = new GenerateGraphNRGBreakdownFromInstantState( definition, colorIndex );
		
		String graphFrameTitle = new FrameTitleGenerator().genFramePrefix( namePrefix, definition.getTitle() );
		
		try {
			CfgNRGInstantStateGraphPanel tablePanel = new CfgNRGInstantStateGraphPanel(generator);
			
			StatePanelFrameHistoryCfgNRGInstantState frame = new StatePanelFrameHistoryCfgNRGInstantState(graphFrameTitle, true);
			frame.init(
				defaultStateManager.getState().getLinkState().getFrameIndex(),
				finderCfgNRGHistory.get(),
				tablePanel,
				errorReporter
			);
			
			return frame.moduleCreator();

		} catch (GetOperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		} catch (InitException e) {
			throw new VideoStatsModuleCreateException(e);
		}
		
	}

	@Override
	public String title() {
		return definition.getTitle();
	}

	@Override
	public String shortTitle() {
		return definition.getShortTitle();
	}
}
