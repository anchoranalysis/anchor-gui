package org.anchoranalysis.gui.bean.filecreator;

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


import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileMultiCollection;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.input.MultiInput;

public class NamedMultiCollectionCreator extends FileCreatorGeneralList {

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<MultiInput> input;
	// END BEAN PROPERTIES
	
	@Override
	public void addFilesToList(List<InteractiveFile> listFiles,
			FileCreatorParams params, ProgressReporter progressReporter) throws OperationFailedException {
		
		try {
			Iterator<MultiInput> itr = input.inputObjects(
				new InputManagerParams(
					params.createInputContext(),
					progressReporter,
					params.getLogErrorReporter()
				)
			).iterator();
			
			while ( itr.hasNext() ) {
			
				MultiInput obj = itr.next();	
				
				FileMultiCollection file = new FileMultiCollection(
					obj,
					params.getMarkCreatorParams()
				);
				listFiles.add(file);
			}
			
		} catch (AnchorIOException e) {
			throw new OperationFailedException(e);
		}
	}

	@Override
	public String suggestName() {
		
		if (hasCustomName()) {
			return getCustomName();
		}
		
		return "untitled stack/cfg collection";
	}

	public InputManager<MultiInput> getInput() {
		return input;
	}

	public void setInput(InputManager<MultiInput> input) {
		this.input = input;
	}
}
