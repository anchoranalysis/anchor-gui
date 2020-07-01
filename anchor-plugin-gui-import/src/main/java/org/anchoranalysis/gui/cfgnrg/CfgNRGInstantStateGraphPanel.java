package org.anchoranalysis.gui.cfgnrg;

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


import javax.swing.JPanel;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.gui.plot.panel.GraphPanel;

public class CfgNRGInstantStateGraphPanel extends StatePanel<CfgNRGInstantState> {

	private GraphPanel graphPanel;

	private FunctionWithException<CfgNRGInstantState,ClickableGraphInstance,OperationFailedException> graphGenerator;
	
	public CfgNRGInstantStateGraphPanel( FunctionWithException<CfgNRGInstantState,ClickableGraphInstance,OperationFailedException> graphGenerator ) {
		this.graphGenerator = graphGenerator;

	}
	
	
	@Override
	public JPanel getPanel() {
		return graphPanel.getPanel();
	}

	@Override
	public void updateState(CfgNRGInstantState state) throws StatePanelUpdateException {
		
		try {
			ClickableGraphInstance graphInstance = graphGenerator.apply(state);
			
			if (graphPanel==null) {
				this.graphPanel = new GraphPanel( graphInstance );
			} else {
				this.graphPanel.updateGraph(graphInstance);
			}
		} catch (OperationFailedException e) {
			throw new StatePanelUpdateException(e);
		}
	}

	@Override
	public IPropertyValueSendable<IntArray> getSelectMarksSendable() {
		return null;
	}

	


	@Override
	public IPropertyValueReceivable<IntArray> getSelectMarksReceivable() {
		return null;
	}


	@Override
	public IPropertyValueReceivable<OverlayCollection> getSelectOverlayCollectionReceivable() {
		return null;
	}


	@Override
	public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
		return null;
	}
}
