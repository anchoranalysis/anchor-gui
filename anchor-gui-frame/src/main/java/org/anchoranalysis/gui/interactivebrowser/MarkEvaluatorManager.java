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

import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.bean.mpp.MarkEvaluator;
import org.anchoranalysis.image.stack.Stack;

// Manages the various MarkEvaluators that are available in the application
public class MarkEvaluatorManager {

	private Map<String,MarkEvaluator> map = new HashMap<>(); 

	private LogErrorReporter logErrorReporter;
	private RandomNumberGenerator re;
	
	public MarkEvaluatorManager(LogErrorReporter logErrorReporter, RandomNumberGenerator re) {
		super();
		this.logErrorReporter = logErrorReporter;
		this.re = re;
	}

	public Set<String> keySet() {
		return map.keySet();
	}
	
	public MarkEvaluatorSetForImage createSetForStackCollection(
		OperationWithProgressReporter<INamedProvider<Stack>> namedImgStackCollection,
		Operation<KeyValueParams> keyParams
	) throws CreateException {
		
		try {
			MarkEvaluatorSetForImage out = new MarkEvaluatorSetForImage(
				namedImgStackCollection,
				keyParams,
				logErrorReporter,
				re
			);
			
			for( String key : map.keySet()) {
				MarkEvaluator me = map.get(key);
				out.add( key, me.duplicateBean() );
			}
			return out;
			
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}
	
	public void add( String key, MarkEvaluator item ) {
		map.put( key, item );
	}

	public boolean hasItems() {
		return map.size() > 0;
	}

}
