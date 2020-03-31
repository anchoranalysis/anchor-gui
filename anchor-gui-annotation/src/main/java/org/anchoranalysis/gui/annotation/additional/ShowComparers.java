package org.anchoranalysis.gui.annotation.additional;

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

import java.nio.file.Path;
import java.util.List;

import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.annotation.io.bean.comparer.MultipleComparer;
import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

public class ShowComparers {

	private ShowRaster showRaster;
	private MultipleComparer multipleComparer;
	private ColorSetGenerator colorSetGenerator;
	private Path matchPath;
	private String name;
	private IObjectBridge<Integer,DisplayStack> defaultBackground;
	private LogErrorReporter logErrorReporter;

	public ShowComparers(ShowRaster showRaster, MultipleComparer multipleComparer, ColorSetGenerator colorSetGenerator,
			Path matchPath, String name,
			IObjectBridge<Integer, DisplayStack> defaultBackground, LogErrorReporter logErrorReporter) {
		super();
		this.showRaster = showRaster;
		this.multipleComparer = multipleComparer;
		this.colorSetGenerator = colorSetGenerator;
		this.matchPath = matchPath;
		this.name = name;
		this.defaultBackground = defaultBackground;
		this.logErrorReporter = logErrorReporter;
	}	
	
	public void apply( AnnotationWithCfg annotationExst ) {
		// Any comparisons to be done
		if (multipleComparer!=null && annotationExst!=null ) {
			showMultipleComparers(annotationExst);
		}		
	}
	
	private void showMultipleComparers( AnnotationWithCfg annotationExst ) {
				
		List<INameValue<Stack>> rasters;
		try {
			DisplayStack background = defaultBackground.bridgeElement(0);  // createBackgroundFromSet( operationBackgroundSet );
			rasters = multipleComparer.createRasters(
				annotationExst,
				background,
				matchPath,
				colorSetGenerator,
				logErrorReporter,
				false
			);
		} catch (CreateException | BridgeElementException e1) {
			logErrorReporter.getErrorReporter().recordError(AnnotatorModuleCreator.class, e1);
			return;
		}
		
		for( final INameValue<Stack> ni : rasters ) {
				
			showRaster.show(
				progressReporter -> createBackgroundSet(ni.getValue()),
				rasterName(ni.getName())
			);
		}
	}
	
	private String rasterName( String rasterName ) {
		return String.format("%s: %s", name, rasterName);		
	}
	
	private static BackgroundSet createBackgroundSet( Stack stack ) throws ExecuteException {
		try {
			BackgroundSet backgroundSet = new BackgroundSet();
			backgroundSet.addItem("Associated Raster", stack);
			return backgroundSet;
			
		} catch (OperationFailedException e) {
			throw new ExecuteException(e);
		}		
	}
}
