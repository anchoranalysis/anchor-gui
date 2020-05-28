package org.anchoranalysis.gui.finder;

/*
 * #%L
 * anchor-image-io
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


import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;

public class OperationFindNrgStackFromStackCollection extends CachedOperation<NRGStackWithParams,OperationFailedException> implements OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> {

	// We first retrieve a namedimgcollection which we use to contstruct our real NrgStack for purposes
	//   of good caching
	private OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> operationStackCollection;
	
	// An operation to retrieve a stackCollection
	//
	public OperationFindNrgStackFromStackCollection(
		OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> operationStackCollection
	) {
		super();
		this.operationStackCollection = operationStackCollection;
	}
	
	public OperationWithProgressReporter<NamedProvider<Stack>,OperationFailedException> getOperationStackCollection() {
		return operationStackCollection;
	}

	@Override
	public NRGStackWithParams doOperation(ProgressReporter progressReporter) throws OperationFailedException {
		return doOperation();
	}

	@Override
	protected NRGStackWithParams execute() throws OperationFailedException {
		return createNRGStack();
	}
	
	// NB Note assumption about namedImgStackCollection ordering
	private NRGStackWithParams createNRGStack() throws OperationFailedException {
		
		NamedProvider<Stack> nic = operationStackCollection.doOperation( ProgressReporterNull.get() );
		
		// We expects the keys to be the indexes
		Stack stack = populateStack(nic);
		
		if (stack.getNumChnl()>0) {
			return new NRGStackWithParams(stack);
		} else {
			return null;
		}
	}
	
	private static Stack populateStack( NamedProvider<Stack> nic ) throws OperationFailedException {
		
		Stack stack = new Stack();
		
		int size = nic.keys().size();
		for( int c=0; c<size; c++ ) {
			
			try {
				stack.addChnl(
					chnlFromStack(nic,c)
				);
			} catch (IncorrectImageSizeException e) {
				throw new OperationFailedException(e);
			}
		}
		
		return stack;
	}
	
	/** Retrieves a single-channel from a named-provider assuming the following:
	 * 
	 *  The channel exists in a single-raster stack
	 *  The stack has a name equal to an integer
	 * 
	 * @param stackProvider dictionary of stacks
	 * @param c the integer that is the name of the stack
	 * @return
	 * @throws CreateException 
	 */
	private static Chnl chnlFromStack( NamedProvider<Stack> stackProvider, int c ) throws OperationFailedException {
		
		try {
			Stack chnlStack = stackProvider.getException( Integer.toString(c) );
			
			if (chnlStack.getNumChnl()!=1) {
				throw new OperationFailedException("Stack should have only a single channel");
			}
			
			return chnlStack.getChnl(0);
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e);
		}
	}
}