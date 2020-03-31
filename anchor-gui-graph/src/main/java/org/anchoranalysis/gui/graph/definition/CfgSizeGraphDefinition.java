package org.anchoranalysis.gui.graph.definition;

/*-
 * #%L
 * anchor-gui-graph
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;

public class CfgSizeGraphDefinition extends GraphDefinition {

	/**
	 * 
	 */

    private double sizeCrnt;
    private int sizeBest;
    
    private int windowSize;
    
    
    
    public CfgSizeGraphDefinition(int windowSize) {
		super();
		this.windowSize = windowSize;
	}

	@Override
	public String title() {
    	return "Configuration Size";
    }
    
    @Override
	public SimpleXYChartDescriptor descriptor() {
    	SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(0, 100, 0, 1d, true, windowSize);
        descriptor.addLineItems("Configuration Size (best)");
        descriptor.addLineItems("Configuration Size (current)");

        descriptor.setDetailsItems(new String[]{
        		"Iteration",
        		"Time",
        		"Configuration Size (best)",
        		"Configuration Size (current)"
        		});
        
        setTitleAndAxes( descriptor, title(), "time", "number");
        
        return descriptor;
    }
    
    @Override
	public long[] valueArr( int iter, long timeStamp ) {
		
		long[] values = new long[2];
		values[0] = this.sizeBest;
		values[1] = (long) this.sizeCrnt;
		return values;
	}
	
	@Override
	public String[] detailsArr( int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support ) {
		return new String[]{
			iter + "",
			support.formatTime( timeStamp - timeZoneOffset ),
			String.format("%4.1f", (double) this.sizeBest),
			String.format("%4.1f", this.sizeCrnt)
            };
	}

	
    @Override
	public void updateCrnt( int iter, long timeStamp, CfgWithNrgTotal crnt, Aggregator agg ) {
   	 	 this.sizeCrnt = agg.getSize();
    }
    
    @Override
	public void updateBest( int iter, long timeStamp, CfgWithNrgTotal best ) {
    	this.sizeBest = best.getCfg().size();
   }
}
