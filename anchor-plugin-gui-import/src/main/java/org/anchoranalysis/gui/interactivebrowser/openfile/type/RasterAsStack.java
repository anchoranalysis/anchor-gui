package org.anchoranalysis.gui.interactivebrowser.openfile.type;

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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.StackCollectionCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.input.Stacks;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.provider.file.FileList;
import org.anchoranalysis.io.bean.input.Files;

public class RasterAsStack extends OpenFileType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String[] getExtensions() {
		return new String[]{ "tif", "tiff", "jpeg", "jpg", "png", "gif", "bmp" };
	}

	@Override
	public String getDescription() {
		return "Raster as Stack";
	}

	@Override
	public List<FileCreator> creatorForFile(List<File> files, ImporterSettings importerSettings) throws CreateException {
		
		FileList fileList = createFileList(files);
		
		Files fileInputManager = new Files();
		fileInputManager.setFileProvider(fileList);
		
		// Until we eliminate RasterReaderETHMetadata class we use a cast
		RasterReader reader = RegisterBeanFactories.getDefaultInstances().get(RasterReader.class);
		
		Stacks inputManager = new Stacks(); 
		inputManager.setRasterReader( reader );
		inputManager.setFileInput(fileInputManager);
		inputManager.setUseLastSeriesIndexOnly(true);
		
		StackCollectionCreator creator = new StackCollectionCreator();
		creator.setCustomName( String.format("raster-set: %s", createName(files)) );
		creator.setInput(inputManager);
		
		List<FileCreator> out = new ArrayList<>();
		out.add( creator );
		return out;
	}



}
