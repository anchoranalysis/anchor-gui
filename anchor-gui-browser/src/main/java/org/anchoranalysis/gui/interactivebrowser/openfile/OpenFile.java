/*-
 * #%L
 * anchor-gui-browser
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
package org.anchoranalysis.gui.interactivebrowser.openfile;



import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.interactivebrowser.openfile.type.OpenFileType;

public class OpenFile extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private Component parentComponent; 
	private Logger logger;
	private FileCreatorLoader fileCreatorLoader;
	private OpenFileTypeFactory factory;
	
	private JFileChooser fileChooser;
	
	public OpenFile( Frame parentComponent, FileCreatorLoader fileCreatorLoader, OpenFileTypeFactory factory, Logger logger ) {
		super("Open File...", new IconFactory().icon("/toolbarIcon/file_open.png") );
		this.parentComponent = parentComponent;
		this.fileCreatorLoader = fileCreatorLoader;
		this.logger = logger;
		this.factory = factory;
		
		//Create a file chooser
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		
		Iterator<OpenFileType> itr = factory.iterator(); 
		while( itr.hasNext() ) {
			OpenFileType oft = itr.next();
			fileChooser.addChoosableFileFilter( new FileNameExtensionFilter(oft.getDescription(), oft.getExtensions()));	
		}
		
		setDefaultDirectory();
		
		putValue(SHORT_DESCRIPTION, "Open File");
	}
	
	
	private void setDefaultDirectory() {
		try {
			// Get current working directory
			File file = new File(".");
			if (Files.exists( Paths.get(file.getCanonicalPath()))) {
				fileChooser.setCurrentDirectory( file);
			}
		} catch (IOException e) {
			logger.errorReporter().recordError(OpenFile.class, e);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Prompt user for a file
		int returnVal = fileChooser.showOpenDialog(parentComponent);
		
		if (returnVal== JFileChooser.APPROVE_OPTION) {
			File[] files = fileChooser.getSelectedFiles();
			
			ArrayList<File> listFiles = new ArrayList<>();
			for( File f : files ) {
				System.out.printf("Selected file is %s%n",f.getPath());
				listFiles.add(f);
			}
			
			if (fileChooser.getFileFilter()!=null) {
				openFileCreator(
					listFiles,
					factory.factoryFromDescription(
						fileChooser.getFileFilter().getDescription()
					),
					parentComponent,
					fileCreatorLoader.getImporterSettings()
				);				
			} else {
				try {
					OpenFileType guessedType = guessFileTypeFromFiles( listFiles );
					openFileCreator(
						listFiles,
						guessedType,
						parentComponent,
						fileCreatorLoader.getImporterSettings()
					);
				} catch (OperationFailedException e1) {
					System.out.printf("Cannot guess filetype: %s\n", e1.toString() );
				}
				
			}
			
			
		}
	}
	
	private OpenFileType guessFileTypeFromFiles(List<File> files) throws OperationFailedException {
		
		OpenFileType fileTypeChosen = null;
		
		for( File f : files ) {
			OpenFileType oft = factory.guessTypeFromFile(f);
			
			if (oft==null) {
				throw new OperationFailedException( String.format("Cannot guess fileType for %s",f.getPath()) ); 
			}
			
			if (fileTypeChosen==null) {
				fileTypeChosen = oft;
			} else {
				// we check it's the same
				if (!fileTypeChosen.equals(oft)) {
					throw new OperationFailedException("Multiple File Types detected");
				}
			}
		}
		
		return fileTypeChosen;
	}
	
	// 
	public void openFileCreator(
		List<File> files,
		OpenFileType fileType,
		Component parentComponent,
		ImporterSettings importerSettings
	) {

		assert( files.size() > 0 );
		
		try {
			if (fileType==null) {
				fileType = guessFileTypeFromFiles(files);
			}
			
			if (fileType==null) {
				showError("FileType not supported");
			}
			
			fileCreatorLoader.addFileListSummaryModule(
				fileType.creatorForFile(
					files,
					importerSettings
				),
				parentComponent
			);
			
		} catch (CreateException e) {
			logger.errorReporter().recordError(OpenFile.class, e);
			showError("An error occurred opening the bean. See log.");
		} catch (OperationFailedException e) {
			logger.errorReporter().recordError(OpenFile.class, e);
			showError("An error occurred opening the bean. See log.");
		}
		
	}
	
	private void showError( String errorMessage ) {
		JOptionPane.showMessageDialog(parentComponent, errorMessage, "Unable to open file", JOptionPane.ERROR_MESSAGE);
	}
	
}
