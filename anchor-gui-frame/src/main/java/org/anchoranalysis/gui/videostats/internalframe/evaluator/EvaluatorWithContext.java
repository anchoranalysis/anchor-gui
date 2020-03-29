package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class EvaluatorWithContext {
	private CfgGen cfgGen;
	private ProposalOperationCreator evaluator;
	private NRGStackWithParams nrgStack;
	private RegionMap regionMap;
			
	public EvaluatorWithContext(ProposalOperationCreator evaluator, NRGStackWithParams nrgStack, CfgGen cfgGen, RegionMap regionMap) {
		super();
		this.nrgStack = nrgStack;
		this.evaluator = evaluator;
		this.cfgGen = cfgGen;
		this.regionMap = regionMap;
	}
	
	public CfgGen getCfgGen() {
		return cfgGen;
	}
	public void setCfgGen(CfgGen cfgGen) {
		this.cfgGen = cfgGen;
	}

	public ProposalOperationCreator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(ProposalOperationCreator evaluator) {
		this.evaluator = evaluator;
	}

	public NRGStackWithParams getNrgStack() {
		return nrgStack;
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}

	public void setRegionMap(RegionMap regionMap) {
		this.regionMap = regionMap;
	}
}