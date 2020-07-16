/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.annotation.dropdown;


import java.io.File;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperation;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

class ExportAnnotationOperation implements VideoStatsOperation {

	private JFrame parentFrame;
	private ExportAnnotation exportAnnotation;
	private AnnotationRefresher annotationRefresher;
	
	public ExportAnnotationOperation(JFrame parentFrame, ExportAnnotation exportAnnotation, AnnotationRefresher annotationRefresher) {
		super();
		this.parentFrame = parentFrame;
		this.exportAnnotation = exportAnnotation;
		this.annotationRefresher = annotationRefresher;
	}

	@Override
	public String getName() {
		if (exportAnnotation.isPromptForScalingNeeded()) {
			return "Export scaled annotation...";
		} else {
			return "Export annotation...";
		}
	}
	
	@Override
	public void execute(boolean withMessages) {
							
		Double scaleFactor = promptForScaleFactor();
		
		if (scaleFactor==null) {
			return;
		}
		
		promptSaveDialog(scaleFactor);
	}
	
	// A null indicates a cancellation
	private static Double promptForScaleFactor() {
		// Choose a XY scale factor
		String scaleFactorText = JOptionPane.showInputDialog("Enter a XY scale-factor (e.g. 2 multiples both X and Y dimensions by 2)","1.0");
		if (scaleFactorText==null || scaleFactorText.isEmpty()) {
			return null;
		}
		
		double sc = Double.parseDouble(scaleFactorText);
		if (!Double.isNaN(sc)) {
			return sc;
		} else {
			return null;
		}
	}
	
	private void promptSaveDialog( double scaleFactor ) {
		
		JFileChooser fc = createFileChooser();
		int retVal = fc.showSaveDialog(parentFrame);
		if (retVal==JFileChooser.APPROVE_OPTION) {
			doExport( fc.getSelectedFile(), scaleFactor );
		}		
	}
	
	private void doExport( File outFile, double scaleFactor  ) {
		try {
			exportAnnotation.exportToPath(outFile.toPath(), scaleFactor, parentFrame, annotationRefresher);
			
			//AnnotationUtilities.deleteAnnotationFileIncludingTemporary(annotationPath);
			//annotationRefresher.refreshAnnotation();
		} catch (OperationFailedException | NumberFormatException e) {
			//custom title, error icon
			JOptionPane.showMessageDialog(parentFrame,
			    String.format("Export of annotation at file '%s' failed%n%n%s",
			    outFile.toPath(),
			    e.toString()),
		    "Error deleting annotation",
		    JOptionPane.ERROR_MESSAGE);
		}						
		
	}
	
	private JFileChooser createFileChooser() {
		// Choose a file
		File annFile = exportAnnotation.proposedExportPath().toFile();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory( annFile.getParentFile() );
		fc.setSelectedFile( new File( annFile.getName() )  );
		fc.setDialogTitle("Export annotation to...");
		return fc;
	}
	

	@Override
	public Optional<IVideoStatsOperationCombine> getCombiner() {
		return Optional.empty();
	}
	
}
