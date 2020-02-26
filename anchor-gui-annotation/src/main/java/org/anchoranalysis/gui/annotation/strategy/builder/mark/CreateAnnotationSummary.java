package org.anchoranalysis.gui.annotation.strategy.builder.mark;

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

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.annotation.state.AnnotationProgressState;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.io.error.AnchorIOException;

class CreateAnnotationSummary {
	
	private static Color colorRed = new Color(99, 00, 00);
	private static Color colorGreen = new Color(00, 99, 00);
	private static Color colorGreenSkipped = new Color(41, 171, 135);
	private static Color colorOrange = new Color(207, 83, 00);
	//private static Color colorOrangeDark = new Color(255, 153, 00);
	
	public static AnnotationSummary apply(Path annotationPath, MarkAnnotationReader annotationReader) throws CreateException {
		AnnotationSummary as = new AnnotationSummary();
		
		AnnotationProgressState aps = annotationProgressState(annotationPath);
		
		if (aps==AnnotationProgressState.ANNOTATION_FINISHED) {
			
			try {
				MarkAnnotation a = annotationReader.read( annotationPath );
				as.setShortDescription( shortDescription(a)	);
				as.setColor( color(aps, a.isAccepted()) );
				as.setExistsFinished(true);
				
			} catch (AnchorIOException e) {
				throw new CreateException(e);
			}
		} else {
			as.setColor( color(aps,false) );
			as.setShortDescription("");
			as.setExistsFinished(false);
		}
		
		return as;		
	}
	
	private static String shortDescription( MarkAnnotation annotation ) {
		if (annotation.isAccepted()) {
			return Integer.toString( annotation.getCfg().size() );
		} else {
			return "";	// Empty-string if not-accepted
		}
	}
	
	private static AnnotationProgressState annotationProgressState(Path annotationPath) {
		
		if (Files.exists(annotationPath)) {
			return AnnotationProgressState.ANNOTATION_FINISHED;
		} else if (Files.exists(Paths.get(annotationPath + ".temp"))) {
			return AnnotationProgressState.ANNOTATION_UNFINISHED;
		}  else {
			return AnnotationProgressState.NO_ANNOTATION;
		}
	}
	
	private static Color color( AnnotationProgressState state, boolean accepted ) {
		
		if (state==AnnotationProgressState.ANNOTATION_FINISHED) {
			
			if (accepted) {
				return colorGreen;
			} else {
				return colorGreenSkipped;
			}
			
		} else if (state==AnnotationProgressState.ANNOTATION_UNFINISHED) {
			return colorOrange;
		} else {
			return colorRed;
		}
	}
}
