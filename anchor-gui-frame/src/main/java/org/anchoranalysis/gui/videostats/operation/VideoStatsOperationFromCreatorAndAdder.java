package org.anchoranalysis.gui.videostats.operation;

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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

public class VideoStatsOperationFromCreatorAndAdder extends VideoStatsOperation {

	private String name;
	private VideoStatsModuleCreatorAndAdder creatorAndAdder;
	private InteractiveThreadPool threadPool;
	private LogErrorReporter logErrorReporter;
	
	public VideoStatsOperationFromCreatorAndAdder(String name, VideoStatsModuleCreatorAndAdder creatorAndAdder, InteractiveThreadPool threadPool, LogErrorReporter logErrorReporter ) {
		this.name = name;
		this.creatorAndAdder = creatorAndAdder;
		this.threadPool = threadPool;
		this.logErrorReporter = logErrorReporter;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void execute(boolean withMessages) {
		creatorAndAdder.createVideoStatsModuleForAdder(threadPool,null,logErrorReporter);
	}

	@Override
	public IVideoStatsOperationCombine getCombiner() {
		return creatorAndAdder.getCreator().getCombiner();
	}

	@Override
	public boolean canCombineOperations() {
		return creatorAndAdder.getCreator().canCombineOperations();
	}
}
