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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IAcceptProposal;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IChangeSelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pointsfitter.InsufficientPointsException;
import ch.ethz.biol.cell.mpp.mark.pointsfitter.PointsFitter;
import ch.ethz.biol.cell.mpp.mark.pointsfitter.PointsFitterException;

public class SelectPointsTool extends AnnotationTool {

	private EvaluatorWithContext evaluator;
	private IChangeSelectedPoints changeSelectedPoints;
	private PointsFitter pointsFitter;
	private IAcceptProposal acceptProposal;
	private ToolErrorReporter errorReporter;
	private IQuerySelectedPoints selectedPoints;
	
	private ImageDim dim;
	
	public SelectPointsTool(
		EvaluatorWithContext evaluator,
		IChangeSelectedPoints changeSelectedPoints,
		PointsFitter pointsFitter,
		IAcceptProposal acceptProposal,
		IQuerySelectedPoints selectedPoints,
		ToolErrorReporter errorReporter
	) {
		super();
		this.evaluator = evaluator;
		this.changeSelectedPoints = changeSelectedPoints;
		this.pointsFitter = pointsFitter;
		this.acceptProposal = acceptProposal;
		this.errorReporter = errorReporter;
		this.selectedPoints = selectedPoints;
	}

	@Override
	public void leftMouseClickedAtPoint(Point3d pnt) {

	}

	@Override
	public void proposed(ProposedCfg proposedCfg) {
		
		dim = proposedCfg.getDimensions();
		
		// Extract what should be the only mark
		assert( proposedCfg.getCfgCore().getMarks().size() ==1 );
		Mark m = proposedCfg.getCfgCore().getMarks().get(0);
		
		changeSelectedPoints.addSelectedPoint( m );
	}

	@Override
	public void confirm(boolean accepted) {
		
		if (acceptProposal.confirm(accepted)) {
			return;	// DONE
		}

		if (selectedPoints.hasSelectedPoints()) {
			
			if (dim==null) {
				errorReporter.showError(
					SelectPointsTool.class,
					"Incorrect initialization",
					"dimensions are null"
				);
			}
			
			proposeCfgFromPoints();
		}
	}
	
	private void proposeCfgFromPoints() {
		
		try {
			changeSelectedPoints.addCurrentProposedCfgFromSelectedPoints( proposeMark() );
		} catch (PointsFitterException e) {
			
			if (e.getCause()==null) {
				errorReporter.showError( SelectPointsTool.class, "Unknown error", e.getMessage() );	
			} else {
				// If we have a cause behind it, we don't know what error is causing it, but
				//   for the sake of giving user instructions we give this message, which
				//   will usually solve the problem
				//   especially if it's  *IllegalArgumentException: Matrix is singular*
				errorReporter.showError("Add more points (in many z-stacks)!" );
			}
			
		} catch (InsufficientPointsException e) {
			errorReporter.showError( "Add more points (in many z-stacks)!" );
		}		
	}
	
	private Mark proposeMark() throws PointsFitterException, InsufficientPointsException {
		Mark mark = evaluator.getCfgGen().getTemplateMark().create();
		pointsFitter.fit(selectedPoints.selectedPointsAsFloats(), mark, dim);
		return mark;
	}

	@Override
	public EvaluatorWithContext evaluatorWithContextGetter() {
		return evaluator;
	}

}
