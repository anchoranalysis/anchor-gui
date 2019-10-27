package org.anchoranalysis.gui.videostats.internalframe.evaluator;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.proposer.visualization.ICreateProposalVisualization;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.evaluator.EvaluatorUtilities;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.MarkAbstractPosition;

public class MarkProposerEvaluatorUtilities {

	public static Mark createMarkFromPosition( Point3d position, Mark templateMark, final ImageDim dim, final RandomNumberGenerator re ) {
		
		final Mark me = templateMark.duplicate();

		if (!(me instanceof MarkAbstractPosition)) {
			throw new IllegalArgumentException("templateMark is not MarkAbstractPosition");
		}
		
		MarkAbstractPosition meCast = (MarkAbstractPosition) me;
		meCast.setPos(position);
		
		return me;
	}
	
	public static ColoredCfg generateCfgFromMark( Mark m, Point3d position, MarkProposer markProposer, boolean detailedVisualization ) {
		
		ColoredCfg cfg = new ColoredCfg();
		
		if (m!=null) {
			cfg.addChangeID(m, new RGBColor(Color.BLUE) );
			addMaskAtMousePoint(position, cfg, m.numDims()==3); 
		}
	
		ICreateProposalVisualization proposalVisualization = markProposer.proposalVisualization(detailedVisualization); 
		if (proposalVisualization!=null) {
			proposalVisualization.addToCfg( cfg );
		}

		return cfg;
	}
	
	private static void addMaskAtMousePoint(Point3d position, ColoredCfg cfg, boolean do3D) {
		Mark mousePoint = EvaluatorUtilities.createMarkFromPoint3d(position, 1, do3D);
		cfg.addChangeID(mousePoint, new RGBColor(Color.GREEN) );
	}
}
