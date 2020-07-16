/* (C)2020 */
package org.anchoranalysis.plugin.gui.graph;

import org.anchoranalysis.anchor.plot.bean.GraphDefinition;

public interface RasterGraph<GraphItem, SourceType> {

    GraphDefinition<GraphItem> getGraphDefinition();

    void setGraphDefinition(GraphDefinition<GraphItem> graphDefinition);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);
}
