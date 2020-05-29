package org.anchoranalysis.gui.annotation;

import java.util.Optional;

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

import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.DualCfg;

/** An annotation for initialising the GUI dialog */
public class InitAnnotation {

	private Optional<AnnotationWithCfg> annotation;
	private DualCfg initCfg;
	private String initMsg;		// A message that loads at the start;
	
	public InitAnnotation(Optional<AnnotationWithCfg> annotation) {
		this(annotation, null, "");
	}
	
	public InitAnnotation(Optional<AnnotationWithCfg> annotation, DualCfg initCfg) {
		this(annotation, initCfg, "");
	}
	
	public InitAnnotation(Optional<AnnotationWithCfg> annotation, DualCfg cfg, String initMsg) {
		super();
		this.annotation = annotation;
		this.initCfg = cfg;
		this.initMsg = initMsg;
	}

	public DualCfg getInitCfg() {
		return initCfg;
	}

	public String getInitMsg() {
		return initMsg;
	}

	public Optional<AnnotationWithCfg> getAnnotation() {
		return annotation;
	}	
}
