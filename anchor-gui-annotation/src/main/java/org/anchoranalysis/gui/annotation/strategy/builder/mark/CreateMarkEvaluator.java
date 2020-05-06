package org.anchoranalysis.gui.annotation.strategy.builder.mark;

/*-
 * #%L
 * anchor-gui-annotation
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

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.plugin.annotation.bean.strategy.GeneratorPathRslvr;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;

class CreateMarkEvaluator {

	public static MarkAnnotator apply(
		MarkEvaluatorManager markEvaluatorManager,
		Path pathForBinding,
		MarkProposerStrategy strategy,
		OperationWithProgressReporter<INamedProvider<Stack>,CreateException> stacks,
		LogErrorReporter logErrorReporter
	) throws CreateException {
		
		MarkEvaluatorSetForImage set = createSet(
			markEvaluatorManager,
			pathForBinding,
			strategy,
			stacks
		);
		
		return new MarkAnnotator(
			strategy,
			set,
			logErrorReporter
		);
	}
	
	public static MarkEvaluatorSetForImage createSet(
		MarkEvaluatorManager markEvaluatorManager,
		Path pathForBinding,
		MarkProposerStrategy strategy,
		OperationWithProgressReporter<INamedProvider<Stack>,CreateException> stacks
	) throws CreateException {
		try {
			return markEvaluatorManager.createSetForStackCollection(
				stacks,
				opLoadKeyValueParams(pathForBinding, strategy)
			);
		} catch (AnchorIOException e) {
			throw new CreateException(e);
		}
	}
	
	private static Operation<KeyValueParams,IOException> opLoadKeyValueParams( Path pathForBinding, MarkProposerStrategy strategy ) throws AnchorIOException {
		Path kvpPath = new GeneratorPathRslvr( pathForBinding ).pathOrNull(
			strategy.getKeyValueParamsFilePathGenerator()
		);
		return () -> create(kvpPath);
	}
		
	private static KeyValueParams create(Path kvpPath) throws IOException {
		if (kvpPath==null) {
			return new KeyValueParams();
		}
		
		return KeyValueParams.readFromFile(kvpPath);
	}
}
