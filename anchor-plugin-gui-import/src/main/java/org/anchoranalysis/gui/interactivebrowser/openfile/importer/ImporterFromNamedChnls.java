package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import java.io.File;

import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.image.io.input.StackInput;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.plugin.io.bean.input.chnl.NamedChnlsBase;

public class ImporterFromNamedChnls extends ImporterFromBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isApplicable(Object bean) {
		return bean instanceof NamedChnlsBase;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FileCreator create(Object bean, File file) {
		return XMLBeanListHelper.createSingleStack(
			(InputManager<? extends StackInput>) bean,
			file
		);
	}

}
