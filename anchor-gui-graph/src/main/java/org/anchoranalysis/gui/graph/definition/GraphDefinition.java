package org.anchoranalysis.gui.graph.definition;

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


import org.anchoranalysis.gui.videostats.ICfgNRGUpdater;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;

public abstract class GraphDefinition implements ICfgNRGUpdater {
    
    public abstract String title();

    protected static void setTitleAndAxes( SimpleXYChartDescriptor descriptor, String title, String xAxis, String yAxis ) {
    	
    	  descriptor.setChartTitle("<html><font size='+1'><b>" + title + "</b></font></html>");
          descriptor.setXAxisDescription("<html>" + xAxis + "</i></html>");
          descriptor.setYAxisDescription("<html>" + yAxis + "</i></html>");
    }
    
    public abstract SimpleXYChartDescriptor descriptor();
    public abstract long[] valueArr( int iter, long timeStamp );
    public abstract String[] detailsArr( int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support );

}