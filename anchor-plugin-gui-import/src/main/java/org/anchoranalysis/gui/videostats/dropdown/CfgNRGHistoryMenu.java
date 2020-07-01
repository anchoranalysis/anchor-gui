package org.anchoranalysis.gui.videostats.dropdown;

import java.io.IOException;

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


import javax.swing.JFrame;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.mpp.plot.bean.GraphDefinitionBarNRGBreakdown;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
import org.anchoranalysis.gui.io.loader.manifest.finder.CfgNRGFinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.creator.BridgedGraphCfgSizeCreator;
import org.anchoranalysis.gui.plot.creator.BridgedGraphNRGCreator;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.NRGTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.ColoredOutlineCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.GraphCSVStatisticModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.GraphDualFinderCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.GraphNRGBreakdownModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromExportTask;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.AllKernelAccptCSVStatistic;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsKernelAccptProb;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsKernelProp;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTemperature;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTime;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTimePerIter;

// TODO needs heavy refactoring for readability
public class CfgNRGHistoryMenu {

	//private static Log log = LogFactory.getLog(CfgNRGHistoryMenu.class);
	
	private final IAddModuleToMenu adder;
	
	// Exposed creators
	private VideoStatsModuleCreatorAndAdder creatorNRGTable = null;
	private VideoStatsModuleCreatorAndAdder creatorColoredOutline = null;
	
	private VideoStatsOperationMenu menu;
	
	public CfgNRGHistoryMenu( VideoStatsOperationMenu parentMenu, String name, IAddModuleToMenu adder ) {
		this.adder = adder;
		this.menu = parentMenu.createSubMenu(name,true);
	}
	
	public void init( 
		FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
		ContainerGetter<CfgNRGInstantState> finderSecondaryHistory,	// Can be NULL if there's no secondary
		ContainerGetter<CfgNRGInstantState> finderTertiaryHistory,	// Can be NULL if there's no secondary
		NRGBackground nrgBackground,
		FinderCSVStats finderCSVStats,
		CfgNRGFinderContext context
	) throws InitException, MenuAddException {
				
		assert( finderCfgNRGHistory.exists() );
		assert( context.getFinderImgStackCollection().exists() );
		
		// Colored Outline
		creatorColoredOutline = adder.addModuleToMenu(
			menu,
			new SingleContextualModuleCreator(
				new ColoredOutlineCreator(finderCfgNRGHistory, nrgBackground.getBackgroundSet())
			),
			false,
			context.getMpg()
		);
		
		if (nrgBackground.getNrgStack()!=null) {
			// NRG Table
			creatorNRGTable = adder.addModuleToMenu(
				menu,
				new SingleContextualModuleCreator(
					new NRGTableCreator(
						finderCfgNRGHistory.getAsOperation(),
						nrgBackground.getNrgStack(),
						context.getMpg().getDefaultColorIndexForMarks()
					)
				),
				false,
				context.getMpg()					
			);
		}
		
		menu.addSeparator();
		
		addGraphs(
			finderCfgNRGHistory,
			context.getFinderKernelProposer(),
			finderCSVStats,
			context.getMpg()
		);
		
		addExportTasks(
			finderCfgNRGHistory,
			context.getFinderImgStackCollection(),
			finderSecondaryHistory,
			finderTertiaryHistory,
			finderCSVStats,
			context.getMpg().getDefaultColorIndexForMarks(),
			context.getMpg().getExportTaskList(),
			context.getOutputManager(),
			context.getParentFrame(),
			context.getMpg().getLogErrorReporter().getErrorReporter()
		);
	}
	
	private void addExportTasks(
		FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
		FinderImgStackCollection finderImgStackCollection,
		ContainerGetter<CfgNRGInstantState> finderSecondaryHistory,
		ContainerGetter<CfgNRGInstantState> finderTertiaryHistory,
		FinderCSVStats finderCSVStats,
		ColorIndex colorIndex,	
		ExportTaskList exportTaskList,
		BoundOutputManagerRouteErrors outputManager,
		JFrame parentFrame,
		ErrorReporter errorReporter
	)
	{		
		VideoStatsOperationMenu exportSubMenu = menu.createSubMenu("Export",true);

		ExportTaskParams exportTaskParams = new ExportTaskParams();
		exportTaskParams.setColorIndexMarks( colorIndex );
		exportTaskParams.addFinderCfgNRGHistory( finderCfgNRGHistory );
		exportTaskParams.setFinderImgStackCollection( finderImgStackCollection );
		exportTaskParams.setFinderCsvStatistics( finderCSVStats );
		exportTaskParams.addFinderCfgNRGHistory( finderSecondaryHistory );
		exportTaskParams.addFinderCfgNRGHistory( finderTertiaryHistory );
		exportTaskParams.setOutputManager(outputManager);
		
		for (ExportTaskBean exportTask : exportTaskList) {
			
			if (exportTask.hasNecessaryParams(exportTaskParams)) {
				
				try {
					ExportTaskBean exportTaskDup = exportTask.duplicateBean();
					
					exportSubMenu.add( new VideoStatsOperationFromExportTask( exportTaskDup, exportTaskParams, parentFrame, errorReporter ) );
					
				} catch (BeanDuplicateException e) {
					errorReporter.recordError(CfgNRGHistoryMenu.class, e);
				}
			}
		}
	}
	
	private void addGraphs(
		final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
		final FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
		final FinderCSVStats finderCSVStats,
		final VideoStatsModuleGlobalParams mpg
	) throws MenuAddException
	{	
		final VideoStatsOperationMenu graphSubMenu = menu.createSubMenu("Graphs",false);
		
		
		
		// We only add these items after the item has been selected, as it can be time consuming to do
		//  the finderKernelProposer.get()
		graphSubMenu.addMenuListener( new ExecuteWhenMenuFirstSelected(mpg.getLogErrorReporter().getErrorReporter()) {

			private void addCSVStatistic( VideoStatsOperationMenu subMenu, GraphDefinition<CSVStatistic> graphDefinition, boolean useShortNames ) throws MenuAddException {
				adder.addModuleToMenu(
					subMenu,
					new SingleContextualModuleCreator(
						new GraphCSVStatisticModuleCreator( graphDefinition, finderCSVStats )
					),
					false,
					mpg
				);
			}
			
			@Override
			public void execute() throws OperationFailedException {
				try {
					adder.addModuleToMenu( graphSubMenu, new GraphDualFinderCreator<>( new BridgedGraphNRGCreator(), finderCfgNRGHistory, finderCSVStats, mpg.getGraphColorScheme() ), false, mpg);
					adder.addModuleToMenu( graphSubMenu, new GraphDualFinderCreator<>( new BridgedGraphCfgSizeCreator(), finderCfgNRGHistory, finderCSVStats, mpg.getGraphColorScheme()), false, mpg );
					/*addModule( graphSubMenu, new GraphCSVStatisticModuleCreator( new GraphDefinitionLineIterVsAccptProb(), finderCSVStats), false );
					addModule( graphSubMenu, new GraphCSVStatisticModuleCreator( new GraphDefinitionLineIterVsAccptProbAll(), finderCSVStats), false );
					addModule( graphSubMenu, new GraphCSVStatisticModuleCreator( new GraphDefinitionLineIterVsAccptProbRand(), finderCSVStats ), false );
					addModule( graphSubMenu, new GraphCSVStatisticModuleCreator( new GraphDefinitionLineIterVsAccptProbMultipleSeries(), finderCSVStats ), false);*/
					addCSVStatistic( graphSubMenu, new GraphDefinitionLineIterVsTemperature(mpg.getGraphColorScheme()), false );
					addCSVStatistic( graphSubMenu, new GraphDefinitionLineIterVsTime(mpg.getGraphColorScheme()), false );
					addCSVStatistic( graphSubMenu, new GraphDefinitionLineIterVsTimePerIter(mpg.getGraphColorScheme()), false );
				} catch (MenuAddException e) {
					throw new OperationFailedException(e);
				}
				
				if (finderKernelProposer.exists() && finderCSVStats.exists()) {
					
					VideoStatsOperationMenu acceptedSubMenu = graphSubMenu.createSubMenu("Kernel - Rate of Acceptance",false);

					String kernelNames[];
					try {
						KernelProposer<CfgNRGPixelized> kp = finderKernelProposer.get();
						
						// TODO could be called many times, consider better strategy
						kp.init();
						kernelNames = kp.createKernelFactoryNames();
						addCSVStatistic( acceptedSubMenu, new GraphDefinitionLineIterVsKernelAccptProb("multiple series", kernelNames, new AllKernelAccptCSVStatistic(), mpg.getGraphColorScheme() ), true );
						acceptedSubMenu.addSeparator();
					} catch (MenuAddException | InitException | IOException e) {
						throw new OperationFailedException(e);
					}
					
					try {
						for (int i=0; i<finderKernelProposer.get().getNumKernel(); i++ ) {
							addCSVStatistic(acceptedSubMenu,  new GraphDefinitionLineIterVsKernelAccptProb(finderKernelProposer.get(), i, mpg.getGraphColorScheme()), true );
						}
					} catch (MenuAddException | IOException e) {
						throw new OperationFailedException(e);
					}
				}
				
				if (finderKernelProposer.exists() && finderCSVStats.exists()) {
					
					VideoStatsOperationMenu propSubMenu = graphSubMenu.createSubMenu("Kernel - Rate of Proposal",false);
					try {
						for (int i=0; i<finderKernelProposer.get().getNumKernel(); i++ ) {
							addCSVStatistic(propSubMenu, new GraphDefinitionLineIterVsKernelProp(finderKernelProposer.get(), i, mpg.getGraphColorScheme()), true );
						}
					} catch (MenuAddException | IOException e) {
						throw new OperationFailedException(e);
					}
				}
			}
			
		});
		
		// Only add the rest of the statistics if, we know how many kernels we are looking for - we can read the header names, if we don't
		//  have a kernel proposer, but for now let's do it this way
		if (finderKernelProposer.exists() && finderCSVStats.exists()) {
			
		}
		
		try {
			GraphDefinitionBarNRGBreakdown graphNRGBreakdown = new GraphDefinitionBarNRGBreakdown();
			graphNRGBreakdown.setGraphColorScheme(mpg.getGraphColorScheme());
			adder.addModuleToMenu(
				graphSubMenu,
				new SingleContextualModuleCreator(
					new GraphNRGBreakdownModuleCreator( graphNRGBreakdown, finderCfgNRGHistory, mpg.getDefaultColorIndexForMarks() )
				),
				false,
				mpg
			);
		} catch (InitException e) {
			throw new MenuAddException(e);
		}
	}

	public VideoStatsModuleCreatorAndAdder getCreatorNRGTable() {
		return creatorNRGTable;
	}

	public VideoStatsModuleCreatorAndAdder getCreatorColoredOutline() {
		return creatorColoredOutline;
	}

	public VideoStatsOperationMenu getMenu() {
		return menu;
	}

}
