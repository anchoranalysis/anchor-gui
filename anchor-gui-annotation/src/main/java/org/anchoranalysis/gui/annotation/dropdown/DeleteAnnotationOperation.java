package org.anchoranalysis.gui.annotation.dropdown;

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
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.anchoranalysis.annotation.io.mark.MarkAnnotationDeleter;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperation;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

class DeleteAnnotationOperation implements VideoStatsOperation {

	private JFrame parentFrame;
	private Path pathToDelete;
	private AnnotationRefresher annotationRefresher;
	
	public DeleteAnnotationOperation(JFrame parentFrame, Path pathToDelete, AnnotationRefresher annotationRefresher) {
		super();
		this.parentFrame = parentFrame;
		this.pathToDelete = pathToDelete;
		this.annotationRefresher = annotationRefresher;
	}

	@Override
	public String getName() {
		return "Delete annotation";
	}

	@Override
	public void execute(boolean withMessages) {
		try {
			new MarkAnnotationDeleter().delete(pathToDelete);
			annotationRefresher.refreshAnnotation();
		} catch (IOException e) {
			//custom title, error icon
			JOptionPane.showMessageDialog(parentFrame,
			    String.format("Deleting annotation at file '%s' failed%n%n%s",
			    pathToDelete,
			    e.toString()),
		    "Error deleting annotation",
		    JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public Optional<IVideoStatsOperationCombine> getCombiner() {
		return Optional.empty();
	}

	
}
