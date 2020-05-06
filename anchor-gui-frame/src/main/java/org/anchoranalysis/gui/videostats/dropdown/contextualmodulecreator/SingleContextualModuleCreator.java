package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class SingleContextualModuleCreator extends ContextualModuleCreator {

	private VideoStatsModuleCreatorContext moduleCreator;
	
	public SingleContextualModuleCreator(VideoStatsModuleCreatorContext moduleCreator) {
		super();
		this.moduleCreator = moduleCreator;
	}
	
	@Override
	public NamedModule[] create(
		String namePrefix,
		OperationWithProgressReporter<IAddVideoStatsModule,? extends Throwable> adder,
		VideoStatsModuleGlobalParams mpg
	)
			throws CreateException {

		NamedModule namedModuleSingle = createSingle(namePrefix, adder, mpg );
		
		if (namedModuleSingle==null) {
			return new NamedModule[]{}; 
		}
		
		return new NamedModule[]{
			namedModuleSingle
		};
	}
	
	public NamedModule createSingle(String namePrefix, OperationWithProgressReporter<IAddVideoStatsModule,? extends Throwable> adder,
			VideoStatsModuleGlobalParams mpg) {

		if (!moduleCreator.precondition()) {
			return null;
		}
				
		VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder(
			adder,
			moduleCreator.resolve(namePrefix, mpg)
		);
		
		String shortTitle = moduleCreator.shortTitle();
		if (shortTitle!=null) {
			return new NamedModule(moduleCreator.title(), creatorAndAdder, shortTitle);
		} else {
			return new NamedModule(moduleCreator.title(), creatorAndAdder);
		}
	}

}
