package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrameHistoryCfgNRGInstantState;
import org.anchoranalysis.gui.cfgnrgtable.CfgNRGTablePanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class NRGTableCreator extends VideoStatsModuleCreatorContext {

	private final Operation<LoadContainer<CfgNRGInstantState>,GetOperationFailedException> operation;
	private final ColorIndex colorIndex;
	private final Operation<NRGStackWithParams,OperationFailedException> nrgStackWithParams;
	
	public NRGTableCreator(
		Operation<LoadContainer<CfgNRGInstantState>,GetOperationFailedException> operation,
		Operation<NRGStackWithParams,OperationFailedException> nrgStackWithParams,
		ColorIndex colorIndex
	) {
		super();
		this.operation = operation;
		this.colorIndex = colorIndex;
		this.nrgStackWithParams = nrgStackWithParams;
	}

	@Override
	public boolean precondition() {
		return (colorIndex!=null && nrgStackWithParams!=null);
	}

	@Override
	public IModuleCreatorDefaultState moduleCreator(DefaultModuleStateManager defaultStateManager, String namePrefix,
			VideoStatsModuleGlobalParams mpg) throws VideoStatsModuleCreateException {
		
		try {
			LoadContainer<CfgNRGInstantState> cntr = operation.doOperation();
			
			StatePanelFrameHistoryCfgNRGInstantState frame = new StatePanelFrameHistoryCfgNRGInstantState( namePrefix, !cntr.isExpensiveLoad() );
			frame.init(
				defaultStateManager.getLinkStateManager().getState().getFrameIndex(),
				cntr,
				new CfgNRGTablePanel( colorIndex, nrgStackWithParams.doOperation() ),
				mpg.getLogErrorReporter().getErrorReporter()
			);
			frame.controllerSize().configureSize(300,600, 300, 1000);
			return frame.moduleCreator();
			
		} catch (IllegalArgumentException | InitException | GetOperationFailedException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}

	@Override
	public String title() {
		return "NRG Table";
	}

	@Override
	public String shortTitle() {
		return null;
	}
}
