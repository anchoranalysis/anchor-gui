/* (C)2020 */
package org.anchoranalysis.gui.plot.definition;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import org.anchoranalysis.gui.videostats.ICfgNRGUpdater;

public abstract class GraphDefinition implements ICfgNRGUpdater {

    public abstract String title();

    protected static void setTitleAndAxes(
            SimpleXYChartDescriptor descriptor, String title, String xAxis, String yAxis) {

        descriptor.setChartTitle("<html><font size='+1'><b>" + title + "</b></font></html>");
        descriptor.setXAxisDescription("<html>" + xAxis + "</i></html>");
        descriptor.setYAxisDescription("<html>" + yAxis + "</i></html>");
    }

    public abstract SimpleXYChartDescriptor descriptor();

    public abstract long[] valueArr(int iter, long timeStamp);

    public abstract String[] detailsArr(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support);
}
