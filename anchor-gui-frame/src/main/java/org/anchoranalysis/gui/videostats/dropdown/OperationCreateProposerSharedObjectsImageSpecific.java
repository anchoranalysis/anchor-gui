package org.anchoranalysis.gui.videostats.dropdown;



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


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.anchor.mpp.bean.init.GeneralInitParams;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.stack.Stack;

public class OperationCreateProposerSharedObjectsImageSpecific extends CachedOperation<MPPInitParams> {

	private OperationWithProgressReporter<INamedProvider<Stack>> namedImgStackCollection;
	private Operation<KeyValueParams> keyParams;
	
	private Define namedDefinitions;
	
	private GeneralInitParams paramsGeneral;
	
	public OperationCreateProposerSharedObjectsImageSpecific(
			OperationWithProgressReporter<INamedProvider<Stack>> namedImgStackCollection,
			Operation<KeyValueParams> keyParams,
			Define namedDefinitions,
			GeneralInitParams paramsGeneral
			) {
		super();
		this.namedImgStackCollection = namedImgStackCollection;
		this.paramsGeneral = paramsGeneral;
		this.namedDefinitions = namedDefinitions;
		this.keyParams = keyParams;
	}

	// If we've created the proposerShared objects, then we return the names of the available stacks
	// If not, we simply return all possible names
	public Set<String> namesStackCollection() {
		
		if (isDone()) {
			return this.getResult().getImage().getStackCollection().keys();
		} else {
			Set<String> out = new HashSet<>();
			out.addAll( namesFromListNamedItems( namedDefinitions.getList(StackProvider.class) ));
			
			try {
				out.addAll( namedImgStackCollection.doOperation( ProgressReporterNull.get() ).keys() );
			} catch (ExecuteException e) {
				paramsGeneral.getLogErrorReporter().getErrorReporter().recordError(OperationCreateProposerSharedObjectsImageSpecific.class, e);
			}
			return out;
		}
	}
	
	@Override
	protected MPPInitParams execute() throws ExecuteException {

		// We initialise the markEvaluator
		try {
			SharedObjects so = new SharedObjects( paramsGeneral.getLogErrorReporter() );			
			
			MPPInitParams soMPP = MPPInitParams.create(
				so,
				namedDefinitions,
				paramsGeneral
			);
			ImageInitParams soImage = soMPP.getImage();
			
			soImage.copyStackCollectionFrom( namedImgStackCollection.doOperation( ProgressReporterNull.get() ) );
			soImage.addToKeyValueParamsCollection("input_params", keyParams.doOperation());
			
			return soMPP;

		} catch (OperationFailedException | CreateException e) {
			throw new ExecuteException(e);
		}
	}

	private static Set<String> namesFromListNamedItems( List<NamedBean<AnchorBean<?>>> list) {
		
		HashSet<String> out = new HashSet<>();
		for( NamedBean<?> item : list ) {
			out.add( item.getName() );
		}
		return out;
	}
}
