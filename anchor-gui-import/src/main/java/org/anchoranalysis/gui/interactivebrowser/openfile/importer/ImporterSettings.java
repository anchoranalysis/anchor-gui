/*-
 * #%L
 * anchor-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;


import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.OpenFileType;

public class ImporterSettings extends AnchorBean<ImporterSettings> {

	// START BEAN FIELDS
	@BeanField
	private List<ImporterFromBean> beanImporters;
	
	@BeanField
	private List<OpenFileType> openFileImporters;
	// END BEAN FIELDS

	public List<ImporterFromBean> getBeanImporters() {
		return beanImporters;
	}

	public void setBeanImporters(List<ImporterFromBean> beanImporters) {
		this.beanImporters = beanImporters;
	}

	public List<OpenFileType> getOpenFileImporters() {
		return openFileImporters;
	}

	public void setOpenFileImporters(List<OpenFileType> openFileImporters) {
		this.openFileImporters = openFileImporters;
	}
}
