package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

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

import java.awt.Component;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.annotation.mark.RejectionReason;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.gui.annotation.save.ISaveAnnotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQueryAcceptedRejected;

class SaveAnnotationMPP implements ISaveAnnotation<MarkAnnotation> {

	private Path annotationPath;
	
	public SaveAnnotationMPP(Path annotationPath) {
		super();
		this.annotationPath = annotationPath;
	}

	/** Save the annotation in a finished-state */
	@Override
	public void saveFinished( IQueryAcceptedRejected query, AnnotationWriterGUI<MarkAnnotation> annotationWriter, JComponent dialogParent ) {
		
		saveAnnotation(
			annotationWriter,
			annotation -> annotation.markAccepted(
				query.getCfgAccepted(),
				query.getCfgRejected()
			),
			dialogParent
		);
	}
	
	@Override
	public void savePaused( IQueryAcceptedRejected query, AnnotationWriterGUI<MarkAnnotation> annotationWriter, JComponent dialogParent ) {
		
		saveAnnotation(
			annotationWriter,
			annotation -> annotation.markPaused(
				query.getCfgAccepted(),
				query.getCfgRejected()
			),
			dialogParent
		);
	}
	
	@Override
	public void skipAnnotation( IQueryAcceptedRejected query, AnnotationWriterGUI<MarkAnnotation> annotationWriter, JComponent dialogParent ) {
		
		promptForRejectionReason().ifPresent( rejectionReason->
			saveAnnotation(
				annotationWriter,
				annotation -> annotation.markRejected(
					query.getCfgAccepted(),
					query.getCfgRejected(),
					rejectionReason
				),
				dialogParent
			)
		);
	}

	private void saveAnnotation( AnnotationWriterGUI<MarkAnnotation> annotationWriter, Consumer<MarkAnnotation> opAnnotation, Component dialogParent ) {
		
		MarkAnnotation annotation = new MarkAnnotation();
		opAnnotation.accept(annotation);
		annotationWriter.saveAnnotation(annotation, annotationPath, dialogParent );
	}
	

	// Returns NULL if cancelled
	private static Optional<RejectionReason> promptForRejectionReason() {
		
		String[] choices = {"Boundary is incorrect", "Image quality is too poor", "Incorrect image content", "Other", "Cancel"};
		
		 int response = JOptionPane.showOptionDialog(
                 null                       // Center in window.
               , "Why do you skip annotating?"        // Message
               , "Skip annotation"               // Title in titlebar
               , JOptionPane.DEFAULT_OPTION  // Option type
               , JOptionPane.PLAIN_MESSAGE  // messageType
               , null                       // Icon (none)
               , choices                    // Button text as above.
               , "Boundary is incorrect"    // Default button's label
             );

		switch (response) {
		  case 0: 
		      return Optional.of(
		    	 RejectionReason.INCORRECT_BOUNDARY
		      );
		  case 1:
			  return Optional.of(
				 RejectionReason.POOR_IMAGE_QUALITY
			  );
		  case 2:
		      return Optional.of(
		    	 RejectionReason.INCORRECT_IMAGE_CONTENT
		    );
		  case 3:
		      return Optional.of(
		    	 RejectionReason.OTHER
		    );
		  case 4:
		  case -1:
		      return Optional.empty();
		  default:
			  throw new AnchorImpossibleSituationException();
		}
	}
}
