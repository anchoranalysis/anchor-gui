package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

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


import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

public abstract class AnnotationTool {

	//
	// Optional action, when leftMouseClickedoccurs
	//
	// NB when we have a evaluatorWithContextGetter() then the evaluation occurs elsewhere
	//   and we don't need to handle this here
	//
	// So for most purposes one would specify either leftMouseClicked or evaluatorWithContextGetter but not both
	//
	public abstract void leftMouseClickedAtPoint( Point3d pnt );
	
	public abstract void proposed(ProposedCfg proposedCfg);
	
	public abstract void confirm(boolean accepted);
	
	public abstract EvaluatorWithContext evaluatorWithContextGetter();
}
