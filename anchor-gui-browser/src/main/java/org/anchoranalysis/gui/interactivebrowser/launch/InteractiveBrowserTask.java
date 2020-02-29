package org.anchoranalysis.gui.interactivebrowser.launch;

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




import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.TaskWithoutSharedState;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.ParametersBound;
import org.anchoranalysis.gui.interactivebrowser.browser.InteractiveBrowser;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;


public class InteractiveBrowserTask extends TaskWithoutSharedState<InteractiveBrowserInput> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8461975683895621229L;
	
	// START BEAN PROPERTIES
	@BeanField
	private ExportTaskList exportTaskList;
	// END BEAN PROPERTIES

	@Override
	public InputTypesExpected inputTypesExpected() {
		return new InputTypesExpected(InteractiveBrowserInput.class);
	}
		
	@Override
	public void doJobOnInputObject(ParametersBound<InteractiveBrowserInput,Object> params)	throws JobExecutionException {
		
		try {
			InteractiveBrowser browser = new InteractiveBrowser(
				params.getOutputManager(),
				params.getLogErrorReporter(),
				getExportTaskList()
			);
			browser.init( params.getInputObject() );
			browser.showWithDefaultView();
		} catch (InitException e) {
			throw new JobExecutionException(e);
		}
		
	}
	
	@Override
	public boolean hasVeryQuickPerInputExecution() {
		return true;
	}
	
	public ExportTaskList getExportTaskList() {
		return exportTaskList;
	}

	public void setExportTaskList(ExportTaskList exportTaskList) {
		this.exportTaskList = exportTaskList;
	}
}
