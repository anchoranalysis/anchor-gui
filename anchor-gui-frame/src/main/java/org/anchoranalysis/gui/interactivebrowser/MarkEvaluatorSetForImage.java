package org.anchoranalysis.gui.interactivebrowser;



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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.anchoranalysis.anchor.mpp.bean.init.GeneralInitParams;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.bean.mpp.MarkEvaluator;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateProposerSharedObjectsImageSpecific;
import org.anchoranalysis.image.stack.Stack;

public class MarkEvaluatorSetForImage {

	private Map<String,Operation<MarkEvaluatorRslvd>> map = new HashMap<>(); 

	private OperationWithProgressReporter<INamedProvider<Stack>> namedImgStackCollection;
	private Operation<KeyValueParams> keyParams;
	private GeneralInitParams paramsGeneral;

	public MarkEvaluatorSetForImage(
		OperationWithProgressReporter<INamedProvider<Stack>> namedImgStackCollection,
		Operation<KeyValueParams> keyParams,
		GeneralInitParams paramsGeneral
	) {
		super();
		this.keyParams = keyParams;
		this.namedImgStackCollection = namedImgStackCollection;
		this.paramsGeneral = paramsGeneral;
	}

	private class Rslv extends CachedOperation<MarkEvaluatorRslvd> {

		private OperationCreateProposerSharedObjectsImageSpecific operationProposerSharedObjects;
		private MarkEvaluator me;
		
		public Rslv( MarkEvaluator me ) throws CreateException {
			this.me = me;
			operationProposerSharedObjects =
				new OperationCreateProposerSharedObjectsImageSpecific(
					namedImgStackCollection,
					keyParams,
					/// TODO Do we need this duplication?
					me.getNamedDefinitions().duplicateBean(),
					paramsGeneral
				);
			
			try {
				// TODO owen, this is causing a bug in the annotorator, we need to get our feature params from somewhere else
				//  i.e. where they are being passed around
				me.initRecursive(
					operationProposerSharedObjects.doOperation().getFeature(),
					paramsGeneral.getLogErrorReporter()
				);
			} catch (InitException | ExecuteException e) {
				throw new CreateException(e);
			}
			
		}
		
		@Override
		protected MarkEvaluatorRslvd execute() throws ExecuteException {
			try {
				return new MarkEvaluatorRslvd(
					operationProposerSharedObjects,
					me.getCfgGen(),
					me.getNrgSchemeCreator().create(),
					keyParams.doOperation()
				);
			} catch (CreateException e) {
				throw new ExecuteException(e);
			}
		}
		
	}
	
	public void add( String key, MarkEvaluator me ) throws OperationFailedException {
		try {
			map.put(key, new Rslv(me) );
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}

	public Set<String> keySet() {
		return map.keySet();
	}
	
	public MarkEvaluatorRslvd get(String key) throws GetOperationFailedException {
		try {
			Operation<MarkEvaluatorRslvd> op = map.get(key); 
			
			if (op==null) {
				throw new GetOperationFailedException( String.format("Cannot find markEvaluator '%s'", key) );
			}
			
			return op.doOperation(); 
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	public boolean hasItems() {
		return map.size() > 0;
	}

}
