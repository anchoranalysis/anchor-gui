/*-
 * #%L
 * anchor-gui-plot
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
package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;



import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize.Item;

public class BridgedGraphCfgSizeCreator extends BridgedGraphFromDualFinderCreator<GraphDefinitionLineIterVsCfgSize.Item> {
	
	@Override
	public GraphDefinition<GraphDefinitionLineIterVsCfgSize.Item> createGraphDefinition( GraphColorScheme graphColorScheme ) throws CreateException {
		GraphDefinitionLineIterVsCfgSize graphDefinition = new GraphDefinitionLineIterVsCfgSize();
		graphDefinition.setGraphColorScheme( graphColorScheme );
		return graphDefinition;
	}

	@Override
	public FunctionWithException<CSVStatistic,Item,CreateException> createCSVStatisticBridge() {
		return statistic -> new GraphDefinitionLineIterVsCfgSize.Item(
			statistic.getIter(),
			statistic.getSize()
		);
	}

	@Override
	public FunctionWithException<CfgNRGInstantState,Item,CreateException> createCfgNRGInstantStateBridge() {
		return sourceObject -> {
			if (sourceObject.getCfgNRG()!=null) {
				return new GraphDefinitionLineIterVsCfgSize.Item( sourceObject.getIndex(), sourceObject.getCfgNRG().getCfg().size() );
			} else {
				return new GraphDefinitionLineIterVsCfgSize.Item( sourceObject.getIndex(), Double.NaN );
			}
		};
	}
}
