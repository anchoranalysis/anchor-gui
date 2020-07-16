/*-
 * #%L
 * anchor-plugin-gui-import
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
package org.anchoranalysis.gui.cfgnrgtable;


import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;

public class SummaryTableModel extends TitleValueTableModel implements IUpdateTableData {

	private static final long serialVersionUID = 7092606148170077373L;

	public SummaryTableModel() {
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				return Integer.toString( state.getIndex() );
			}
			
			@Override
			public String genTitle() {
				return "Iteration";
			}
		});
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				if (state.getCfgNRG()!=null) {
					return Integer.toString( state.getCfgNRG().getCfg().size() );
				} else {
					return "";
				}
			}
			
			@Override
			public String genTitle() {
				return "Size";
			}
		});
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				if (state.getCfgNRG()!=null) {
					return String.format("%f", state.getCfgNRG().getNrgTotal() );
				} else {
					return "";
				}
			}
			
			@Override
			public String genTitle() {
				return "NRG Total";
			}
		});
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				if (state.getCfgNRG()!=null) {
					return String.format("%f", state.getCfgNRG().getCalcMarkInd().getNrgTotal() );
				} else {
					return "";
				}
			}
			
			@Override
			public String genTitle() {
				return "NRG Total - Individual";
			}
		});
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				if (state.getCfgNRG()!=null) {
					return String.format("%f", state.getCfgNRG().getCalcMarkPair().getNRGTotal() );
				} else {
					return "";
				}
			}
			
			@Override
			public String genTitle() {
				return "NRG Total - Pairs";
			}
		});
		addEntry( new ITitleValueRow() {
			
			@Override
			public String genValue(CfgNRGInstantState state) {
				if (state.getCfgNRG()!=null) {
					return String.format("%f", state.getCfgNRG().getCalcMarkAll().getNRGTotal() );
				} else {
					return "";
				}
			}
			
			@Override
			public String genTitle() {
				return "NRG Total - All";
			}
		});		
	}

}
