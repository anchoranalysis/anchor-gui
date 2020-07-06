package org.anchoranalysis.gui.annotation;

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


import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.anchoranalysis.annotation.io.bean.input.AnnotationInputManager;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.InteractiveFileListInternalFrame;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;

public class AnnotationListInternalFrame {

	private InteractiveFileListInternalFrame delegate;
	
	private JProgressBar progressBar;
	private AnnotationTableModel annotationTableModel;
	
	public AnnotationListInternalFrame( String name ) {
		delegate = new InteractiveFileListInternalFrame(name);
	}

	/**
	 * 
	 * @param inputManager
	 * @param adder
	 * @param fileOpenManager
	 * @param markEvaluatorManager
	 * @param mpg
	 * @param progressReporter
	 * @param widthDescriptionColumn 1 indicates small, larger numbers make it bigger
	 * @throws InitException
	 */
	public <T extends AnnotatorStrategy> void init(
		AnnotationInputManager<ProvidesStackInput,T> inputManager,
		IAddVideoStatsModule adder,
		IOpenFile fileOpenManager,
		MarkCreatorParams params,
		ProgressReporter progressReporter,
		int widthDescriptionColumn 
	) throws InitException {
		
		try {
			annotationTableModel = new AnnotationTableModel(
				pr -> createProject(
					inputManager,
					params,
					progressReporter
				),
				progressReporter
			);
			delegate.init(
				adder,
				annotationTableModel,
				fileOpenManager,
				params.getModuleParams(),
				params.getMarkDisplaySettings()
			);
						
			progressBar = createProgressBar();
			refreshProgressBar();
			
			delegate.addComponentTop( createPanelFor(progressBar,"Annotation Progress:  ") );
			
			AnnotationTableCellRenderer tableCellRenderer = new AnnotationTableCellRenderer(annotationTableModel);
			delegate.setColumnRenderer(0, tableCellRenderer );
			delegate.setColumnRenderer(1, tableCellRenderer );
			delegate.setColumnRenderer(2, tableCellRenderer );
						
			annotationTableModel.addTableModelListener(e->refreshProgressBar());
		} catch (CreateException e) {
			throw new InitException(e);
		}
		
		delegate.setColumnWidth(0, 2980);		// Name
		delegate.setColumnWidth(1, 100 * widthDescriptionColumn);			// Color
		delegate.setColumnWidth(2, 20);			// Color
	}
	
	private JProgressBar createProgressBar() {
		JProgressBar pbar = new JProgressBar(0, 100);
		pbar.setName("Annotated");
		pbar.setValue(0);
		pbar.setStringPainted(true);
		return pbar;
	}
	
	private void refreshProgressBar() {
		AnnotationProject ap = annotationTableModel.getAnnotationProject();
		int annotated = ap.numAnnotated();
		int size = ap.size();
		progressBar.setString( String.format("%d from %d", annotated, size) );
		progressBar.setMaximum(size);
		progressBar.setValue(annotated);
	}
	
	
	public JPanel createPanelFor( JComponent component, String labelString ) {
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) );
		panel.setLayout( new BorderLayout() );
		panel.add(component, BorderLayout.CENTER );
		panel.add( new JLabel(labelString), BorderLayout.WEST );
		return panel;
	}

	public IModuleCreatorDefaultState moduleCreator() {
		return delegate.moduleCreator();
	}

	private <T extends AnnotatorStrategy> AnnotationProject createProject(
		AnnotationInputManager<ProvidesStackInput,T> inputManager,
		MarkCreatorParams params,
		ProgressReporter progressReporter
	) throws OperationFailedException {
		try {
			Collection<AnnotationWithStrategy<T>> inputObjs = inputManager.inputObjects(
				new InputManagerParams(
					params.getModuleParams().createInputContext(),
					progressReporter,
					params.getModuleParams().getLogErrorReporter()
				)
			); 
			
			return new AnnotationProject(
				inputObjs,
				params.getMarkEvaluatorManager(),
				params.getModuleParams(),
				ProgressReporterNull.get()
			);
			
		} catch (AnchorIOException | IOException | CreateException e) {
			throw new OperationFailedException(e);
		}
	}
}
