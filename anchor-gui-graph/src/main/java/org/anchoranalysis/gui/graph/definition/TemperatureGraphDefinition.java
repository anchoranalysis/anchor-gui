package org.anchoranalysis.gui.graph.definition;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;

public class TemperatureGraphDefinition extends GraphDefinition {

	/**
	 * 
	 */

    private double temperature;
    
    private int windowSize;
    
    public TemperatureGraphDefinition(int windowSize) {
		super();
		this.windowSize = windowSize;
	}

	private long rslv( double nrg ) {
    	return (long) (100 * nrg);
    }
    
    @Override
	public String title() {
    	return "Temperature";
    }
    
    @Override
	public SimpleXYChartDescriptor descriptor() {
    	SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(0, 100, 0, 0.01d, true, windowSize);
        descriptor.addLineFillItems("Temperature");

        descriptor.setDetailsItems(new String[]{
        		"Iteration",
        		"Time",
        		"Temperature"
        		});
        
        setTitleAndAxes( descriptor, title(), "time", "temperature");
        
        return descriptor;
    }
    
    @Override
	public long[] valueArr( int iter, long timeStamp ) {
		
		long[] values = new long[1];
		values[0] = rslv(this.temperature);
		return values;
	}
	
	@Override
	public String[] detailsArr( int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support ) {
		return new String[]{
			iter + "",
			support.formatTime( timeStamp - timeZoneOffset ),
			String.format("%e", this.temperature),
            };
	}

	
    @Override
	public void updateCrnt( int iter, long timeStamp, CfgWithNrgTotal crnt, Aggregator agg ) {
   	 	 this.temperature = agg.getTemp();
    }
    
    @Override
	public void updateBest( int iter, long timeStamp, CfgWithNrgTotal best ) {
    	
   }
}
