/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
package org.anchoranalysis.gui.annotation;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSet;
import org.anchoranalysis.gui.videostats.link.DefaultLinkStateManager;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

/** The rasters which form the background to the annotations */
public class AnnotationBackground {

	private OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> backgroundSetOp;
	private FunctionWithException<Integer,DisplayStack,GetOperationFailedException> defaultBackground;
	private ImageDimensions dimViewer;
	
	public AnnotationBackground(
		ProgressReporterMultiple prm,
		NamedProvider<Stack> backgroundStacks,
		String stackNameVisualOriginal
	) throws GetOperationFailedException {
		backgroundSetOp = new OperationCreateBackgroundSet(backgroundStacks);
		{
			defaultBackground = backgroundSetOp.doOperation(
				new ProgressReporterOneOfMany(prm)
			).stackCntr(stackNameVisualOriginal);
			
			if (defaultBackground==null) {
				throw new GetOperationFailedException(
					String.format("Cannot find stackName '%s'", stackNameVisualOriginal )
				);
			}
			
			dimViewer = defaultBackground.apply(0).getDimensions();
		}		
	}
		
	public void configureLinkManager( DefaultLinkStateManager linkStateManager ) {
		linkStateManager.setBackground( defaultBackground );
		linkStateManager.setSliceNum( dimViewer.getZ()/2 );		
	}

	public ImageDimensions getDimensionsViewer() {
		return dimViewer;
	}

	public FunctionWithException<Integer,DisplayStack,GetOperationFailedException> getDefaultBackground() {
		return defaultBackground;
	}

	public OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> getBackgroundSetOp() {
		return backgroundSetOp;
	}
}
