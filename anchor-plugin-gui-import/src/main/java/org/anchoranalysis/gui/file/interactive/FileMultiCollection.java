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

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.input.MultiInput;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class FileMultiCollection extends InteractiveFile {

	private MultiInput inputObject;
	private VideoStatsModuleGlobalParams mpg;
	private MarkEvaluatorManager mem;
	
	public FileMultiCollection(
		MultiInput inputObject,
		VideoStatsModuleGlobalParams mpg,
		MarkEvaluatorManager markEvaluatorManager
	) {
		super();
		this.inputObject = inputObject;
		this.mem = markEvaluatorManager;
		this.mpg = mpg;
	}

	@Override
	public String identifier() {
		//return FilenameUtils.removeExtension( new File(inputObject.descriptiveName()).getName() );
		return inputObject.descriptiveName();
	}
	
	@Override
	public File associatedFile() {
		return inputObject.pathForBinding().toFile();
	}

	@Override
	public String type() {
		return "raster";
	}

	@Override
	public OpenedFile open(
			IAddVideoStatsModule globalSubgroupAdder,
			BoundOutputManagerRouteErrors outputManager
		) throws OperationFailedException {
		
		LazyEvaluationStore<TimeSequence> stacks = new LazyEvaluationStore<>(mpg.getLogErrorReporter(),"stacks"); 
		inputObject.stack().addToStore(stacks);
		
		LazyEvaluationStore<Cfg> cfgs = new LazyEvaluationStore<>(mpg.getLogErrorReporter(),"cfg");
		inputObject.cfg().addToStore(cfgs);

		LazyEvaluationStore<KeyValueParams> keyValueParams = new LazyEvaluationStore<>(mpg.getLogErrorReporter(),"keyValueParams");
		inputObject.keyValueParams().addToStore(keyValueParams);
		
		LazyEvaluationStore<ObjMaskCollection> objs = new LazyEvaluationStore<>(mpg.getLogErrorReporter(),"objMaskCollection");
		inputObject.objs().addToStore(objs);
		
		MultiCollectionDropDown dropDown = new MultiCollectionDropDown(
			progressReporter ->	createTimeSequenceProvider(stacks),
			cfgs,
			objs,
			keyValueParams,
			identifier(),
			true
		);
		
		try {
			dropDown.init(
				globalSubgroupAdder,
				mem,
				outputManager,
				mpg
			);
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		return new OpenedFileGUI(this, dropDown.openedFileGUI() );
	}
	
	private TimeSequenceProvider createTimeSequenceProvider( LazyEvaluationStore<TimeSequence> stacks ) throws ExecuteException {
		try {
			return new TimeSequenceProvider(stacks, inputObject.numFrames());
		} catch (OperationFailedException e) {
			throw new ExecuteException(e);
		}
	}

}
