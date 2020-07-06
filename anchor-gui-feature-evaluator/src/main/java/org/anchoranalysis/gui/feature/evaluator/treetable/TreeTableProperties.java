package org.anchoranalysis.gui.feature.evaluator.treetable;

/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.log.Logger;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

public class TreeTableProperties {

	private RowModel rowModel;
	private RenderDataProvider renderDataProvider;
	private String title;
	private Logger logger;

	public TreeTableProperties(RowModel rowModel, RenderDataProvider renderDataProvider, String title,
			Logger logger) {
		super();
		this.rowModel = rowModel;
		this.renderDataProvider = renderDataProvider;
		this.title = title;
		this.logger = logger;
	}

	public RowModel getRowModel() {
		return rowModel;
	}

	public RenderDataProvider getRenderDataProvider() {
		return renderDataProvider;
	}

	public String getTitle() {
		return title;
	}

	public Logger getLogErrorReporter() {
		return logger;
	}
	
}
