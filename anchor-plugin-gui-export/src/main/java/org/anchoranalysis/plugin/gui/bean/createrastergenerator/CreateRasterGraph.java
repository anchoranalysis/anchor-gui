/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;
import org.anchoranalysis.plugin.gui.graph.RasterGraph;

public abstract class CreateRasterGraph<T, S> extends CreateRasterGenerator<S>
        implements RasterGraph<T, S> {

    // START BEAN PARAMETERS
    @BeanField private GraphDefinition<T> graphDefinition;

    @BeanField private int width = 1024;

    @BeanField private int height = 768;
    // END BEAN PARAMETERS

    protected IterableObjectGenerator<GraphInstance, Stack> createGraphInstanceGenerator() {
        return new GraphInstanceGenerator(width, height);
    }

    @Override
    public abstract IterableObjectGenerator<MappedFrom<S>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException;

    @Override
    public abstract boolean hasNecessaryParams(ExportTaskParams params);

    @Override
    public GraphDefinition<T> getGraphDefinition() {
        return graphDefinition;
    }

    @Override
    public void setGraphDefinition(GraphDefinition<T> graphDefinition) {
        this.graphDefinition = graphDefinition;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String getBeanDscr() {
        return String.format(
                "graph=%s, width=%d, height=%d", graphDefinition.getTitle(), width, height);
    }
}
