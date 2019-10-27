package org.anchoranalysis.gui.annotation.export;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFrame;

import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationWriter;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;
import org.apache.commons.io.FilenameUtils;

public class ExportScaledMarks extends ExportAnnotation {

	private MarkAnnotationReader reader = new MarkAnnotationReader(true);
	private Path annotationPath;
	
	public ExportScaledMarks(Path annotationPath) {
		super();
		assert(annotationPath!=null);
		// Read annotation
		this.annotationPath = annotationPath;
	}
	
	@Override
	public boolean isPromptForScalingNeeded() {
		return true;
	}

	@Override
	public Path proposedExportPath() {
		// Appends _export to a path
		return suffixBasename( annotationPath, "_export");
	}
	
	@Override
	public void exportToPath(Path path, double scaleFactor, JFrame parentFrame, AnnotationRefresher annotationRefresher) throws OperationFailedException {

		try {
			// Read file
			MarkAnnotation ann = reader.read(path);
			
			if (ann==null) {
				throw new OperationFailedException("There is no annotation to export");
			}
			
			// Scale annotation
			if (scaleFactor!=1.0) {
				ann.scaleXY( scaleFactor );
			}

			MarkAnnotationWriter writer = new MarkAnnotationWriter();
			writer.setDisablePathModification(true);
			AnnotationWriterGUI<MarkAnnotation> writerGUI = new AnnotationWriterGUI<>(
				writer,
				annotationRefresher,
				null
			);
			writerGUI.saveAnnotation(ann, path, parentFrame);
		} catch (IOException | OptionalOperationUnsupportedException e) {
			throw new OperationFailedException(e);
		}
	}
	
	/** Adds a suffix to the basename of a file (i.e. before the extension
	 *  e.g. /path/to/abc.txt  become /pth/to/abcsuffix.txt
	 * */
	private static Path suffixBasename( Path path, String suffix ) {
		
		String filePath = path.toString();
		
		String basename = FilenameUtils.getBaseName(filePath);
		String extension = FilenameUtils.getExtension(filePath);
		File dir = path.toFile().getParentFile();
		
		return dir.toPath().resolve(basename + suffix + "." + extension);		
	}



}
