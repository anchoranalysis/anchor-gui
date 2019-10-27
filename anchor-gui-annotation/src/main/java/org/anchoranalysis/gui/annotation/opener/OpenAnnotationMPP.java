package org.anchoranalysis.gui.annotation.opener;

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

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.annotation.mark.RejectionReason;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.DualCfg;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class OpenAnnotationMPP implements IOpenAnnotation {
	
	private MarkAnnotationReader annotationReader;
	private Path annotationPath;
	private Path defaultCfgPath;

	public OpenAnnotationMPP(Path annotationPath, Path defaultCfgPath, MarkAnnotationReader annotationReader) {
		super();
		this.annotationPath = annotationPath;
		this.defaultCfgPath = defaultCfgPath;
		this.annotationReader = annotationReader;
	}	
	
	@Override
	public InitAnnotation open(
		boolean useDefaultCfg,
		LogErrorReporter logErrorReporter
	) throws VideoStatsModuleCreateException {
		
		// We try to read an existing annotation
		MarkAnnotation annotationExst  = readAnnotation(annotationPath);
		
		if (annotationExst!=null) {
			return readCfgFromAnnotation(annotationExst);
			
		} else if (defaultCfgPath!=null && useDefaultCfg) {
			return readDefaultCfg(annotationExst, defaultCfgPath, logErrorReporter );
							
		} else {
			logErrorReporter.getLogReporter().logFormatted("No cfg to open for annotation");
			return new InitAnnotation(annotationExst);
		}
	}

	public boolean isUseDefaultPromptNeeded() {
		return !annotationReader.annotationExistsCorrespondTo(annotationPath) && defaultCfgPath!=null;
	}
		
	private static InitAnnotation readCfgFromAnnotation( MarkAnnotation annotationExst ) {
		
		DualCfg initCfg = new DualCfg(
			annotationExst.getCfg(),
			annotationExst.getCfgReject()
		); 
		
		if (annotationExst.isAccepted()) {
			return new InitAnnotation(annotationExst, initCfg);
		} else {
			String initMsg = genErrorMsg(annotationExst.getRejectionReason());
			return new InitAnnotation(annotationExst, initCfg, initMsg);
		}

	}
	
	private InitAnnotation readDefaultCfg( AnnotationWithCfg annotationExst, Path defaultCfgPath, LogErrorReporter logErrorReporter ) {
		try {
			Cfg defaultCfg = annotationReader.readDefaultCfg(defaultCfgPath);
			return new InitAnnotation(
				annotationExst,
				new DualCfg( defaultCfg, new Cfg() )
			);
		} catch (DeserializationFailedException e) {
			logErrorReporter.getLogReporter().logFormatted("Cannot open defaultCfg at %s", defaultCfgPath);
			logErrorReporter.getErrorReporter().recordError(AnnotatorModuleCreator.class, e);
			return new InitAnnotation(annotationExst);
		}
	}
	
	/** A message explaining why the annotation wasn't accepted */
	private static String genErrorMsg(RejectionReason reason) {
		StringBuilder sb = new StringBuilder("Annotation was SKIPPED due to ");
		switch (reason) {
		case INCORRECT_BOUNDARY:
			sb.append("an incorrect boundary");
			break;
		case POOR_IMAGE_QUALITY:
			sb.append("poor image quality");
			break;
		case INCORRECT_IMAGE_CONTENT:
			sb.append("incorrect image content");
			break;
		case OTHER:
			sb.append("undefined reasons");
			break;
		default:
			assert false;
		}
		return sb.toString();
	}
	
	private MarkAnnotation readAnnotation( Path annotationPath ) throws VideoStatsModuleCreateException {
		try	{
			// We try to read an existing annotation
			// If we can read an annotation let's do it
			return annotationReader.read(annotationPath);
		} catch (IOException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}
}
