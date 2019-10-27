package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.ArrayList;

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


import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

class NamesFromBackgroundSet implements IGetNames {

	private OperationWithProgressReporter<BackgroundSet> backgroundSet;
	private ErrorReporter errorReporter;
	
	public NamesFromBackgroundSet(OperationWithProgressReporter<BackgroundSet> backgroundSet, ErrorReporter errorReporter) {
		super();
		this.backgroundSet = backgroundSet;
		this.errorReporter = errorReporter;
	}

	@Override
	public List<String> names() {
		try {
			Set<String> namesSorted = new TreeSet<>(
				backgroundSet.doOperation( ProgressReporterNull.get() ).names()
			);
			return new ArrayList<>(namesSorted);
			
		} catch (ExecuteException e) {
			errorReporter.recordError(NamesFromBackgroundSet.class, e);
			return new ArrayList<>();
		}
	}
	
}