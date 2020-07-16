/*-
 * #%L
 * anchor-plugin-gui-import
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
package org.anchoranalysis.gui.io.loader.manifest.finder;

import javax.swing.JFrame;

import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackgroundAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

public class FinderContext {
	
	private NRGBackgroundAdder<?> nrgBackground;
	private BoundVideoStatsModuleDropDown boundVideoStats;
	private VideoStatsOperationMenu parentMenu;
	private CfgNRGFinderContext context;
	
	public FinderContext(NRGBackgroundAdder<?> nrgBackground, BoundVideoStatsModuleDropDown boundVideoStats,
			VideoStatsOperationMenu parentMenu, CfgNRGFinderContext context) {
		super();
		this.nrgBackground = nrgBackground;
		this.boundVideoStats = boundVideoStats;
		this.parentMenu = parentMenu;
		this.context = context;
	}

	public NRGBackgroundAdder<?> getNrgBackground() {
		return nrgBackground;
	}

	public BoundVideoStatsModuleDropDown getBoundVideoStats() {
		return boundVideoStats;
	}

	public VideoStatsOperationMenu getParentMenu() {
		return parentMenu;
	}

	public CfgNRGFinderContext getContext() {
		return context;
	}

	public VideoStatsModuleGlobalParams getMpg() {
		return context.getMpg();
	}

	public JFrame getParentFrame() {
		return context.getParentFrame();
	}
}
