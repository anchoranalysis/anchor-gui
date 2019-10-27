package org.anchoranalysis.gui.retrieveelements;

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


import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskGenerator;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.bean.exporttask.IExportTask;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread.ExportTaskCommand;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.OperationGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.namestyle.IntegerSuffixOutputNameStyle;

public class ExportSubMenu implements IAddToExportSubMenu {

	private ExportPopupParams params;
	
	private JMenu menu = new JMenu("Export");
	
	private ErrorReporter errorReporter;
	
	public ExportSubMenu( ExportPopupParams params, ErrorReporter errorReporter ) {
		this.params = params;
		this.errorReporter = errorReporter;
		assert(params.getOutputManager()!=null);
	}
	
	@Override
	public void addExportItemStackGenerator( String outputName, String label, Operation<Stack> stack ) throws OperationFailedException {
    	
		StackGenerator stackGenerator = new StackGenerator(true, outputName);
		OperationGenerator<Stack,Stack> generator = new OperationGenerator<Stack,Stack>( stackGenerator );
		addExportItem( generator, stack, outputName, label, generator.createManifestDescription(), 1 );
    }
    
	@Override
    public <T> void addExportItem( IterableGenerator<T> generator, T itemToGenerate, String outputName, String label, ManifestDescription md, int numItems ) {

    	// No parameters available in this context
    	ExportTaskParams exportTaskParams = new ExportTaskParams();
    	exportTaskParams.setCacheMonitor( params.getCacheMonitor() );
    	    	
    	ManifestFolderDescription mfd = new ManifestFolderDescription();
    	mfd.setFileDescription( md );
    	mfd.setSequenceType( new IncrementalSequenceType() );

    	// NB: As bindAsSubFolder can now return nulls, maybe some knock-on bugs are introduced here
    	exportTaskParams.setOutputManager(
    		params.getOutputManager().getWriterAlwaysAllowed().bindAsSubFolder(outputName, mfd, null)
    	);
    	
    	IntegerSuffixOutputNameStyle ons = new IntegerSuffixOutputNameStyle(outputName, 6);
    	
    	IExportTask task = new ExportTaskGenerator<T>(generator, itemToGenerate, params.getParentFrame(), ons, params.getSequenceMemory() );
    			
		ExportTaskActionAsThread action = new ExportTaskActionAsThread(label, new ExportTaskCommand(task, exportTaskParams, params.getParentFrame(), false, errorReporter) );
		menu.add( new JMenuItem(action) );   
    }


	public JMenu getMenu() {
		return menu;
	}
	
	@Override
	public BoundOutputManagerRouteErrors getOutputManager() {
		return params.getOutputManager();
	}
}
