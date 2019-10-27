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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;

import ch.ethz.biol.cell.beaninitparams.MPPInitParams;
import ch.ethz.biol.cell.countchrom.experiment.SharedObjectsUtilities;

public class CreateBackgroundSetFromExisting extends CachedOperationWithProgressReporter<BackgroundSet> {

	private final OperationWithProgressReporter<BackgroundSet> existingBackgroundSet;
	private OperationCreateProposerSharedObjectsImageSpecific pso;
	private OutputWriteSettings ows;
	
	public CreateBackgroundSetFromExisting(
			OperationWithProgressReporter<BackgroundSet> backgroundSet,
			OperationCreateProposerSharedObjectsImageSpecific pso,
			OutputWriteSettings ows
		) {
		super();
		this.existingBackgroundSet = backgroundSet;
		this.pso = pso;
		this.ows = ows;
	}

	@Override
	protected BackgroundSet execute( ProgressReporter progressReporter ) throws ExecuteException {

		BackgroundSet bsExisting = existingBackgroundSet.doOperation(progressReporter);
		try {
			MPPInitParams so = pso.doOperation();
			ImageInitParams soImage = so.getImage();
			
			return BackgroundSetFactory.createBackgroundSetFromExisting(
				bsExisting,
				new WrapStackAsTimeSequence( SharedObjectsUtilities.createCombinedStacks(soImage) ),
				ows,
				progressReporter
			);
			
		} catch (CreateException e) {
			throw new ExecuteException(e);
		}
	}
	
}