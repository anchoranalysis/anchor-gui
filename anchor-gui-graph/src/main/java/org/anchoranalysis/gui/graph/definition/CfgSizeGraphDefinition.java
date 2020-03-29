package org.anchoranalysis.gui.graph.definition;

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
