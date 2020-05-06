package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.RasterModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

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


public class DropDownUtilitiesRaster {
		
	// Note, adds as default
	public static void addRaster(
		VideoStatsOperationMenu menu,
		BoundVideoStatsModuleDropDown delegate,
		NRGBackgroundAdder<?> nrgBackground,
		String name,
		VideoStatsModuleGlobalParams mpg,
		boolean addAsDefault
	) {
		RasterModuleCreator creator = new RasterModuleCreator( nrgBackground.getNRGBackground(), delegate.getName(), name, mpg );
		
		VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder(nrgBackground.getAdder(), creator );
		if (addAsDefault) {
			menu.addAsDefault( new VideoStatsOperationFromCreatorAndAdder(name,creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );
		} else {
			menu.add( new VideoStatsOperationFromCreatorAndAdder(name,creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() ) );
		}
	}
}
