package org.anchoranalysis.plugin.gui.bean.exporttask;

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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;

public class ExportTaskList extends AnchorBean<ExportTaskList> implements Iterable<ExportTaskBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4455432257116909204L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<ExportTaskBean> list = new ArrayList<>();
	// END BEAN PROPERTIES

	public ExportTaskList() {
		
	}

	@Override
	public Iterator<ExportTaskBean> iterator() {
		return list.iterator();
	}

	public boolean add(ExportTaskBean e) {
		return list.add(e);
	}
		
	public List<ExportTaskBean> getList() {
		return list;
	}

	public void setList(List<ExportTaskBean> list) {
		this.list = list;
	}
}
