package org.anchoranalysis.gui.annotation.builder;

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

import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public class AdditionalFramesContext {

	private IAddVideoStatsModule adder;
	private String name;
	private VideoStatsModuleGlobalParams mpg;
	private OutputWriteSettings outputWriteSettings;
	
	public AdditionalFramesContext(IAddVideoStatsModule adder, String name, VideoStatsModuleGlobalParams mpg,
			OutputWriteSettings outputWriteSettings) {
		super();
		this.adder = adder;
		this.name = name;
		this.mpg = mpg;
		this.outputWriteSettings = outputWriteSettings;
	}

	public IAddVideoStatsModule getAdder() {
		return adder;
	}

	public String getName() {
		return name;
	}

	public VideoStatsModuleGlobalParams getMpg() {
		return mpg;
	}

	public OutputWriteSettings getOutputWriteSettings() {
		return outputWriteSettings;
	}	
}
