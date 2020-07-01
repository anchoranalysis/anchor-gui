package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;
import org.anchoranalysis.plugin.gui.graph.RasterGraph;


/**
 * 
 * @author feehano
 *
 * @param <T> graph-item
 * @param <S> source-type
 */
public abstract class CreateRasterGraph<T,S> extends CreateRasterGenerator<S> implements RasterGraph<T, S> {

	// START BEAN PARAMETERS
	@BeanField
	private GraphDefinition<T> graphDefinition;
	
	@BeanField
	private int width = 1024;
	
	@BeanField
	private int height = 768;
	// END BEAN PARAMETERS
	
	protected IterableObjectGenerator<GraphInstance,Stack> createGraphInstanceGenerator() {
		return new GraphInstanceGenerator(width, height);
	}
	
	@Override
	public abstract IterableObjectGenerator<MappedFrom<S>,Stack> createGenerator( ExportTaskParams params ) throws CreateException;

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
		return String.format("graph=%s, width=%d, height=%d", graphDefinition.getTitle(), width, height );
	}
}
