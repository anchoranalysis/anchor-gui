package org.anchoranalysis.gui.videostats.modulecreator;

/*-
 * #%L
 * anchor-gui-frame
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

import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class VideoStatsModuleCreatorContext {

	/** This must evaluate to TRUE before any module is created. Otherwise we abandon creating module */
	public abstract boolean precondition();
	
	public abstract IModuleCreatorDefaultState moduleCreator(DefaultModuleStateManager defaultStateManager, String namePrefix, VideoStatsModuleGlobalParams mpg) throws VideoStatsModuleCreateException;
	
	/** The title of the module. Must be defined. */
	public abstract String title();
	
	/** The short-title of the module. It is allowed return NULL if it none is defined. */
	public abstract String shortTitle();
	
	public VideoStatsModuleCreator resolve( String namePrefix, VideoStatsModuleGlobalParams mpg ) {
		return new VideoStatsModuleCreator() {
			
			@Override
			public void createAndAddVideoStatsModule(IAddVideoStatsModule adder) throws VideoStatsModuleCreateException {
				
				DefaultModuleStateManager defaultStateManager = adder.getSubgroup().getDefaultModuleState();
				
				IModuleCreatorDefaultState creator = moduleCreator(
					defaultStateManager,
					namePrefix,
					mpg
				);
				
				if (creator!=null) {
					ModuleAddUtilities.add(adder, creator );
				}
			}
		};
	}
}
