package org.anchoranalysis.plugin.gui.bean.exporttask;

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


import javax.swing.ProgressMonitor;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.io.generator.sequence.IGeneratorSequenceNonIncremental;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * 
 * @author feehano
 *
 * @param <T> container-type
 */
public class ExportTaskBoundedIndexContainerGeneratorSeries<T> extends AnchorBean<ExportTaskBoundedIndexContainerGeneratorSeries<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PARAMETERS
	@BeanField
	private int incrementSize = 1000;
	
	@BeanField
	private boolean startAtEnd = false;		// We start at the end, and move to the front (should be set with a negative incrementSize)
	
	@BeanField
	private int limitIterations = -1;		// -1 disables
	// END BEAN PARAMETERS
	
	private IObjectBridge<ExportTaskParams,IBoundedIndexContainer<T>> containerBridge;
	
	public ExportTaskBoundedIndexContainerGeneratorSeries() {
	}	
	
	private boolean execute( IBoundedIndexContainer<T> cfgNRGCntr, ProgressMonitor progressMonitor, IGeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter, BoundOutputManagerRouteErrors outputManager, String outputName  ) throws OutputWriteFailedException, GetOperationFailedException {
		
		int min = cfgNRGCntr.getMinimumIndex();
		int max = cfgNRGCntr.getMaximumIndex();
		
		int numAdd = calcIncrements(min, max);
		
		int indexOut = 0;
		IncrementalSequenceType sequenceType = new IncrementalSequenceType(indexOut);
		sequenceType.setIncrementSize( 1 );
		

		sequenceType.setStart(min);
		
		int realNumAdd = numAdd;
		if (limitIterations >= 0) {
			numAdd = Math.min(numAdd,limitIterations);
		}
		
		generatorSequenceWriter.start( sequenceType, realNumAdd );

		
		if (startAtEnd) {
			for (int i=max; i>=min; i-= incrementSize) {
				
				// Escape if we reach our match iterations (limitIterations is off if its negative)
				if (indexOut==limitIterations) {
					break;
				}
					
				if (progressMonitor.isCanceled()) {
					return false;
				}
				
				addToWriter( cfgNRGCntr, generatorSequenceWriter, i, indexOut );
				
				progressMonitor.setProgress(indexOut++);
				
				
			}
		} else {
			for (int i=min; i<=max; i+= incrementSize) {

				// Escape if we reach our match iterations (limitIterations is off if its negative)
				if (indexOut==limitIterations) {
					break;
				}
				
				if (progressMonitor.isCanceled()) {
					return false;
				}
				
				addToWriter( cfgNRGCntr, generatorSequenceWriter, i, indexOut );
				
				progressMonitor.setProgress(indexOut++);
			}			
		}
		progressMonitor.setProgress(max);
		
		generatorSequenceWriter.end();
		
		return true;
	}
	
	private int calcIncrements(int min, int max) {
		double incr = (double) (max-min) / incrementSize;
		return (int) Math.floor( incr ) + 1;
	}
	
	private void addToWriter( IBoundedIndexContainer<T> cntr, IGeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter, int i, int indexOut ) throws OutputWriteFailedException, GetOperationFailedException {
		int index = cntr.previousEqualIndex(i);
		generatorSequenceWriter.add(
			new MappedFrom<>( i, cntr.get(index) ),
			String.valueOf(indexOut)
		);
	}
		
	public boolean execute( ExportTaskParams params, ProgressMonitor progressMonitor, IGeneratorSequenceNonIncremental<MappedFrom<T>> generatorSequenceWriter, BoundOutputManagerRouteErrors outputManager, String outputName ) throws ExportTaskFailedException {
		try {
			return execute( containerBridge.bridgeElement(params), progressMonitor, generatorSequenceWriter, outputManager, outputName);
		} catch (OutputWriteFailedException e) {
			throw new ExportTaskFailedException(e);
		} catch (GetOperationFailedException e) {
			throw new ExportTaskFailedException(e);
		}
	}
	
	public int getMinProgress( ExportTaskParams params ) throws ExportTaskFailedException {
		try {
			return containerBridge.bridgeElement(params).getMinimumIndex();
		} catch (GetOperationFailedException e) {
			throw new ExportTaskFailedException(e);
		}
	}
	
	public int getMaxProgress( ExportTaskParams params ) throws ExportTaskFailedException {
		try {
			return containerBridge.bridgeElement(params).getMaximumIndex();
		} catch (GetOperationFailedException e) {
			throw new ExportTaskFailedException(e);
		}
	}

	public String getDscrContrib() {
		return String.format("incrementSize=%d", this.incrementSize );
	}


	public IObjectBridge<ExportTaskParams, IBoundedIndexContainer<T>> getContainerBridge() {
		return containerBridge;
	}

	public void setContainerBridge(
			IObjectBridge<ExportTaskParams, IBoundedIndexContainer<T>> containerBridge) {
		this.containerBridge = containerBridge;
	}


	public int getIncrementSize() {
		return incrementSize;
	}


	public void setIncrementSize(int incrementSize) {
		this.incrementSize = incrementSize;
	}


	public boolean isStartAtEnd() {
		return startAtEnd;
	}


	public void setStartAtEnd(boolean startAtEnd) {
		this.startAtEnd = startAtEnd;
	}


	public int getLimitIterations() {
		return limitIterations;
	}


	public void setLimitIterations(int limitIterations) {
		this.limitIterations = limitIterations;
	}
}
