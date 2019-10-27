package org.anchoranalysis.gui.file.interactive;

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


import java.io.File;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// An entity that can be opened in the InteractiveBrowser
public abstract class InteractiveFile implements Comparable<InteractiveFile> {

	public abstract String identifier();
	
	// A file associated with this item
	public abstract File associatedFile();
	
	public abstract String type();
	
	public abstract OpenedFile open(
		final IAddVideoStatsModule globalSubgroupAdder,
		final BoundOutputManagerRouteErrors outputManager
	) throws OperationFailedException;
	

	@Override
	public int compareTo(InteractiveFile arg0) {
		assert(arg0.identifier()!=null);
		assert(identifier()!=null);
		return identifier().compareTo(arg0.identifier());
	}
}
