package org.anchoranalysis.gui.bean.exporttask;



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


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;

public class ExportTaskGenerator<IterableType> implements IExportTask {

	private IterableGenerator<IterableType> generator;
	private JFrame parentFrame;
	private IndexableOutputNameStyle outputNameStyle;
	private SequenceMemory sequenceMemory;
	private IterableType item;
	
	public ExportTaskGenerator(IterableGenerator<IterableType> generator, IterableType item, JFrame parentFrame, IndexableOutputNameStyle outputNameStyle, SequenceMemory sequenceMemory ) {
		super();
		this.generator = generator;
		this.parentFrame = parentFrame;
		this.outputNameStyle = outputNameStyle;
		this.sequenceMemory = sequenceMemory;
		this.item = item;
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		//return createRasterGenerator.hasNecessaryParams(params) ;
		return true;
	}
	
	@Override
	public boolean execute(ExportTaskParams params,
			ProgressMonitor progressMonitor) throws ExportTaskFailedException {
		
    	int index = sequenceMemory.lastIndex( outputNameStyle.getOutputName() );
    	
    	try {
			generator.setIterableElement( item );
		} catch (SetOperationFailedException e) {
			throw new ExportTaskFailedException(e);
		}
		
    	int numWritten = params.getOutputManager().getWriterCheckIfAllowed().write(
    		outputNameStyle,
    		()->generator.getGenerator(),
    		index
    	);
		sequenceMemory.updateIndex(outputNameStyle.getOutputName(), index + numWritten );

		if (numWritten==0) {
			JOptionPane.showMessageDialog( parentFrame, "An error occurred" );
		}

		return true;
	}
	
	@Override
	public String getBeanName() {
		return ExportTaskGenerator.class.getSimpleName();
	}

	@Override
	public int getMinProgress(ExportTaskParams params) throws ExportTaskFailedException {
		return 0;
	}

	@Override
	public int getMaxProgress(ExportTaskParams params) throws ExportTaskFailedException {
		return 0;
	}

	@Override
	public String getOutputName() {
		return outputNameStyle.getOutputName();
	}

	@Override
	public void init(CacheMonitor cacheMonitor) {
	}

}
