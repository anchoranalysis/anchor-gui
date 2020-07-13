package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

/*-
 * #%L
 * anchor-gui-frame
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

import java.util.List;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

import lombok.RequiredArgsConstructor;

/** Omits names from the background-map if they contain a substring */
@RequiredArgsConstructor
public class ChangeableBackgroundDefinitionIgnoreContains extends ChangeableBackgroundDefinition {

	// START REQUIRED ARGUMENTS
	private final ChangeableBackgroundDefinition background;
	private final String contains;
	// END REQUIRED ARGUMENTS

	@Override
	public void update(OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> backgroundSet) {
		background.update(backgroundSet);
	}

	@Override
	public IImageStackCntrFromName stackCntrFromName(ErrorReporter errorReporter) {
		return background.stackCntrFromName(errorReporter);
	}
	
	@Override
	public IGetNames names(ErrorReporter errorReporter) {
		IGetNames namesGet = background.names(errorReporter);
		return () -> filterList(
			namesGet.names()
		);
	}
	
	private List<String> filterList( List<String> list ) {
		return FunctionalList.filterToList(list, a -> !a.contains(contains));  
	}
}
