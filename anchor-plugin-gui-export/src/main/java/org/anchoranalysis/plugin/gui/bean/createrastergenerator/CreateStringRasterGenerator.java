package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;

/*-
 * #%L
 * anchor-plugin-gui-export
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public abstract class CreateStringRasterGenerator extends CreateRasterGenerator<CfgNRGInstantState> {

	// START BEAN PROPERTIES
	@BeanField
	private StringRasterGenerator stringGenerator;
	// END BEAN PROPERTIES
	
	@Override
	public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
			ExportTaskParams params) throws CreateException {
		
		return new IterableObjectGeneratorBridge<Stack,MappedFrom<CfgNRGInstantState>,String>(
			stringGenerator.createGenerator(),
			sourceObject -> extractStringFrom( sourceObject.getObj().getCfgNRG() )
		);
	}
	
	protected abstract String extractStringFrom( CfgNRG cfgNRG );
	
	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return true;
	}
	
	public StringRasterGenerator getStringGenerator() {
		return stringGenerator;
	}

	public void setStringGenerator(StringRasterGenerator stringGenerator) {
		this.stringGenerator = stringGenerator;
	}
}
