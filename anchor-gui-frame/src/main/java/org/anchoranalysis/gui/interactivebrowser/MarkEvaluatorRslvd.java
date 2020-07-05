package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;

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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

// A MarkEvaluator after it has been resolved for usage by converting
//  it into a ProposerSharedObjectsImageSpecific and other necessary components
public class MarkEvaluatorRslvd {

	private OperationInitParams operationCreateProposerSharedObjects;
	private OperationNrgStack operationCreateNrgStack;
	private CfgGen cfgGen;
	private NRGScheme nrgScheme;
	
	public MarkEvaluatorRslvd(
			OperationInitParams proposerSharedObjects,
			CfgGen cfgGen,
			NRGScheme nrgScheme,
			KeyValueParams params
			) {
		super();
		this.operationCreateProposerSharedObjects = proposerSharedObjects;
		this.cfgGen = cfgGen;
		this.nrgScheme = nrgScheme;
		
		this.operationCreateNrgStack = new OperationNrgStack(
			operationCreateProposerSharedObjects,
			params
		);
	}

	public OperationInitParams getProposerSharedObjectsOperation() {
		return operationCreateProposerSharedObjects;
	}
	
	public NRGStackWithParams getNRGStack() throws GetOperationFailedException {
		try {
			return operationCreateNrgStack.doOperation();
		} catch (CreateException e) {
			throw new GetOperationFailedException(e);
		}
	}

	public CfgGen getCfgGen() {
		return cfgGen;
	}

	public NRGScheme getNrgScheme() {
		return nrgScheme;
	}
}
