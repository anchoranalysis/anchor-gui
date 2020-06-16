package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.Map;

import org.anchoranalysis.bean.shared.StringMap;

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


import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.stack.DisplayStack;

class StackFromBackgroundSetViaMap implements IImageStackCntrFromName {

	private OperationWithProgressReporter<BackgroundSet,? extends Throwable> backgroundSet;
	private Map<String,String> map;
	private ErrorReporter errorReporter;
	
	public StackFromBackgroundSetViaMap(
		StringMap map,
		OperationWithProgressReporter<BackgroundSet,? extends Throwable> backgroundSet,
		ErrorReporter errorReporter
	) {
		super();
		this.backgroundSet = backgroundSet;
		this.map = map.create();
		this.errorReporter = errorReporter;
	}

	@Override
	public FunctionWithException<Integer, DisplayStack,GetOperationFailedException> imageStackCntrFromName(String name) throws GetOperationFailedException {
		try {				
			return backgroundSet.doOperation( ProgressReporterNull.get() ).stackCntr(
				map.get(name)
			);
		} catch (Throwable e) {
			errorReporter.recordError(NamesFromBackgroundSet.class, e);
			return null;
		}
	}
	
}