package org.anchoranalysis.gui.bean.filecreator;

import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

public class MarkCreatorParams {

	private VideoStatsModuleGlobalParams moduleParams;
	private MarkDisplaySettings markDisplaySettings;
	private MarkEvaluatorManager markEvaluatorManager;
		
	public MarkCreatorParams(VideoStatsModuleGlobalParams moduleParams, MarkDisplaySettings markDisplaySettings,
			MarkEvaluatorManager markEvaluatorManager) {
		super();
		this.moduleParams = moduleParams;
		this.markDisplaySettings = markDisplaySettings;
		this.markEvaluatorManager = markEvaluatorManager;
	}

	public VideoStatsModuleGlobalParams getModuleParams() {
		return moduleParams;
	}
	
	public MarkDisplaySettings getMarkDisplaySettings() {
		return markDisplaySettings;
	}
	
	public MarkEvaluatorManager getMarkEvaluatorManager() {
		return markEvaluatorManager;
	}
}
