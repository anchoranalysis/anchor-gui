package org.anchoranalysis.gui.graph.definition;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;

public class NRGGraphDefinition extends GraphDefinition {

    private double nrgCrnt;
    private double nrgBest;
    
    private int windowSize;
    
    
    
    public NRGGraphDefinition(int windowSize) {
		super();
		this.windowSize = windowSize;
	}

	private long rslv( double nrg ) {
    	return (long) (100 * nrg);
    }
    
    @Override
	public String title() {
    	return "-log Energy";
    }
    
    @Override
	public SimpleXYChartDescriptor descriptor() {
    	SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(-10, 10, -10, 0.01d, true, windowSize);
        descriptor.addLineFillItems("-log Energy (best)");
        descriptor.addLineFillItems("-log Energy (current)");

        descriptor.setDetailsItems(new String[]{
        		"Iteration",
        		"Time",
        		"-log Energy (best)",
        		"-log Energy (current)"
        		});
        
        setTitleAndAxes( descriptor, title(), "time", "energy");
        
        return descriptor;
    }
    
    @Override
	public long[] valueArr( int iter, long timeStamp ) {
		
		long[] values = new long[2];
		values[0] = rslv( this.nrgBest );
		values[1] = rslv( this.nrgCrnt );
		return values;
	}
	
	@Override
	public String[] detailsArr( int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support ) {
		return new String[]{
			iter + "",
			support.formatTime( timeStamp - timeZoneOffset ),
			String.format("%e", this.nrgBest),
			String.format("%e", this.nrgCrnt)
            };
	}

	
    @Override
	public void updateCrnt( int iter, long timeStamp, CfgWithNrgTotal crnt, Aggregator agg ) {
   	 	 this.nrgCrnt = agg.getNrg();
    }
    
    @Override
	public void updateBest( int iter, long timeStamp, CfgWithNrgTotal best ) {
  	 	 this.nrgBest = best.getNrgTotal();
   }
}
