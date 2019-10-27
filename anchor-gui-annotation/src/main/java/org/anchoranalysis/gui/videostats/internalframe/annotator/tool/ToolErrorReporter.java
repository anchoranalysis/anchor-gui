package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

/*-
 * #%L
 * anchor-gui-annotation
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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IShowError;

public class ToolErrorReporter implements IShowError {

	private IShowError showError;
	private ErrorReporter errorReporter;
	
	public ToolErrorReporter(IShowError showError, ErrorReporter errorReporter) {
		super();
		this.showError = showError;
		this.errorReporter = errorReporter;
	}
	
	@Override
	public void showError( String msg ) {
		showError.showError(msg);
	}
	
	public void showError( Class<?> clazz, String msg, String logMsg ) {
		showError.showError(msg);
		errorReporter.recordError( clazz, logMsg );
	}

	@Override
	public void clearErrors() {
		showError.clearErrors();
	}

	public ErrorReporter getErrorReporter() {
		return errorReporter;
	}

}
