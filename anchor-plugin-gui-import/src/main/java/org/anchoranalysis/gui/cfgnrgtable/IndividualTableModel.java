package org.anchoranalysis.gui.cfgnrgtable;

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


import javax.swing.table.AbstractTableModel;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedInd;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;

public class IndividualTableModel extends AbstractTableModel implements IUpdateTableData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7882539188914833799L;

	private Cfg cfg;
	
	private NRGSavedInd nrgSavedInd;
	
	private ColorIndex colorIndex;
	
	public IndividualTableModel( ColorIndex colorIndex ) {
		super();
		this.colorIndex = colorIndex;
	}

	@Override
	public void updateTableData( CfgNRGInstantState state ) {
		
		if (state.getCfgNRG()!=null) {
			this.cfg = state.getCfgNRG().getCfg();
			this.nrgSavedInd = state.getCfgNRG().getCalcMarkInd();
		} else {
			this.cfg = new Cfg();
		}
		fireTableDataChanged();
	}

	@Override
    public String getColumnName(int col) {

		switch(col) {
		case 0:
			return "";
		case 1:
			return "id"; 
		case 2:
			return "nrg";
		default:
			assert false;
			return "error";
		}
    }
	
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
        
    	switch(c) {
		case 0:
			return RGBColor.class;
		case 1:
			return Integer.class; 
		case 2:
			return String.class;
		default:
			assert false;
			return null;
		}
    }
	
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return this.cfg.size();
	}

	public Mark getMark( int index ) {
		return cfg.get( index );
	}
	
	public Cfg getCfg() {
		return cfg;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		
		if (row >= cfg.size()) {
			return "error";
		}
		
		switch(column) {
		case 0:
			// ICON
			return colorIndex.get( cfg.get(row).getId() );
		case 1:
			// ID
			return cfg.get(row).getId(); 
		case 2:
			// NRG
			return String.format("%16.3f",nrgSavedInd.get(row).getTotal());
		default:
			return "error";
		}
	}

}
