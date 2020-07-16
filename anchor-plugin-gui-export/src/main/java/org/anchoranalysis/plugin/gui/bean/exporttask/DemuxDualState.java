/*-
 * #%L
 * anchor-plugin-gui-export
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.plugin.gui.bean.exporttask;


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;

public class DemuxDualState<T> extends CreateRasterGenerator<DualStateWithoutIndex<T>> {

	// START BEAN PROPERTIES
	@BeanField
	private int index = 0;
	
	@BeanField
	private CreateRasterGenerator<T> item;
	// END BEAN PROPERTIES

	private T demux(DualStateWithoutIndex<T> in) {
		return in.getItem(index);
	}
	
	@Override
	public IterableObjectGenerator<MappedFrom<DualStateWithoutIndex<T>>, Stack> createGenerator(ExportTaskParams params)
			throws CreateException {
		
		IterableObjectGenerator<MappedFrom<T>, Stack> generator = item.createGenerator(params);
		
		return new IterableObjectGeneratorBridge<Stack,MappedFrom<DualStateWithoutIndex<T>>,MappedFrom<T>>(
			generator,
			sourceObject -> new MappedFrom<>(
				sourceObject.getOriginalIter(),
				demux( sourceObject.getObj() )
			)
		);
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return true;
	}

	public CreateRasterGenerator<T> getItem() {
		return item;
	}

	public void setItem(CreateRasterGenerator<T> item) {
		this.item = item;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
