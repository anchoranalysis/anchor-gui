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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileSingleStack;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.image.io.input.StackInputBase;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

// A named channel collection derived from a file
public class NamedSingleStackCreator extends FileCreatorGeneralList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<? extends StackInputBase> input;
	// END BEAN PROPERTIES
	
	//private static Log log = LogFactory.getLog(NamedChnlCollectionCreator.class);
	
	@Override
	public void addFilesToList(List<InteractiveFile> listFiles,
			FileCreatorParams params, ProgressReporter progressReporter) throws CreateException {

		try {

			Iterator<? extends StackInputBase> itr = input.inputObjects(
				params.createInputContext(),
				progressReporter
			).iterator();
			
			while ( itr.hasNext() ) {
			
				StackInputBase obj = itr.next();	
				
				FileSingleStack file = new FileSingleStack(
					obj,
					params.getMarkEvaluatorManager(),
					params.getModuleParamsGlobal()
				);
				listFiles.add(file);

			}
			
		} catch (FileNotFoundException e) {
			throw new CreateException(e);
		} catch (IOException e) {
			throw new CreateException(e);
		} catch (DeserializationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	public String suggestName() {
				
		if (hasCustomName()) {
			return getCustomName();
		}
		
		return "untitled raster set";
	}

	public InputManager<? extends StackInputBase> getInput() {
		return input;
	}

	public void setInput(InputManager<? extends StackInputBase> input) {
		this.input = input;
	}
}
