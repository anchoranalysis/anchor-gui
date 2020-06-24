package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

/**
 * 1. Finds the nearest (previous or equal) CSVStatistic
 * 2. Copies it, and updates the iteration to match the current iteration
 * 
 * @author feehano
 *
 */
class FindNearestStatisticBridge implements FunctionWithException<MappedFrom<CfgNRGInstantState>, MappedFrom<CSVStatistic>,OperationFailedException> {
	
	private IBoundedIndexContainer<CSVStatistic> cntr;
		
	public FindNearestStatisticBridge(IBoundedIndexContainer<CSVStatistic> cntr) {
		super();
		this.cntr = cntr;
	}

	@Override
	public MappedFrom<CSVStatistic> apply(MappedFrom<CfgNRGInstantState> sourceObject)
			throws OperationFailedException {
		int indexAdj = cntr.previousEqualIndex(sourceObject.getOriginalIter());
		
		try {
			CSVStatistic stats = cntr.get(indexAdj);
			
			return new MappedFrom<CSVStatistic>(
				sourceObject.getOriginalIter(),
				maybeDuplicate(stats, sourceObject.getOriginalIter())
			);
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private CSVStatistic maybeDuplicate( CSVStatistic stats, int iterToImpose ) {
		// Duplicate and update iteration to match statistics
		if (stats.getIter()==iterToImpose) {
			return stats;
		} else {
			return copyUpdateIter(stats, iterToImpose );
		}
		
	}
	
	private CSVStatistic copyUpdateIter( CSVStatistic stats, int iterToImpose ) {
		CSVStatistic dup = stats.duplicate();
		dup.setIter(iterToImpose);
		return dup;
	}
}
