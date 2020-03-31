package org.anchoranalysis.gui.io.loader.manifest.finder;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import javax.swing.JFrame;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;

public class CfgNRGFinderContext {

	private FinderImgStackCollection finderImgStackCollection;
	private FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer;
	private JFrame parentFrame;
	private BoundOutputManagerRouteErrors outputManager;
	private VideoStatsModuleGlobalParams mpg;
	
	public CfgNRGFinderContext(FinderImgStackCollection finderImgStackCollection,
			FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer, JFrame parentFrame,
			BoundOutputManagerRouteErrors outputManager, VideoStatsModuleGlobalParams mpg) {
		super();
		this.finderImgStackCollection = finderImgStackCollection;
		this.finderKernelProposer = finderKernelProposer;
		this.parentFrame = parentFrame;
		this.outputManager = outputManager;
		this.mpg = mpg;
	}

	public FinderImgStackCollection getFinderImgStackCollection() {
		return finderImgStackCollection;
	}

	public FinderSerializedObject<KernelProposer<CfgNRGPixelized>> getFinderKernelProposer() {
		return finderKernelProposer;
	}

	public JFrame getParentFrame() {
		return parentFrame;
	}

	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}

	public VideoStatsModuleGlobalParams getMpg() {
		return mpg;
	}
	
	
}
