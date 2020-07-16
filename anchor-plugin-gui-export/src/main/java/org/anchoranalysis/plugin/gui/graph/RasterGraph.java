/* (C)2020 */
package org.anchoranalysis.plugin.gui.graph;

import org.anchoranalysis.anchor.plot.bean.GraphDefinition;

/**
 * @author Owen Feehan
 * @param <T> graph-item
 * @param <S> source-type
 */
public interface RasterGraph<T, S> {

    GraphDefinition<T> getGraphDefinition();

    void setGraphDefinition(GraphDefinition<T> graphDefinition);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);
}
