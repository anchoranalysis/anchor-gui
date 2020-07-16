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
package org.anchoranalysis.gui.interactivebrowser;



import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.frame.IAddableToolbar;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

class OpenedFileCounter {

	private IAddableToolbar toolbar;
	private OpenedFile openedFile; 
	private int moduleCnt = 0;
	private VideoStatsModuleMapToOpenedFileCounter mapOpenedFiles;
	private VideoStatsFileMapToOpenedFileCounter mapFile;
	
	public OpenedFileCounter(VideoStatsModuleMapToOpenedFileCounter mapOpenedFiles, VideoStatsFileMapToOpenedFileCounter mapFile, IAddableToolbar toolbar ) {
		super();
		this.toolbar = toolbar;
		this.mapOpenedFiles = mapOpenedFiles;
		this.mapFile = mapFile;
		
		//System.out.println("Creating open file counter");
	}
	
	public void addVideoStatsModule( VideoStatsModule module ) {
		if (moduleCnt==0) {
			toolbar.add( openedFile.getGUI().getButton() );

			//System.out.println("Saving mapFile to map");
			
			// As it's the first module we-add, we should also add the file to the map
			mapFile.put( openedFile.getFile(), this);
		}
		mapOpenedFiles.put(module, this);
		moduleCnt++;
	}
	
	public boolean removeVideoStatsModule( VideoStatsModule module ) {
		OpenedFileCounter ofc = mapOpenedFiles.get(module);
		mapFile.remove( ofc.getOpenedFile().getFile() );
		mapOpenedFiles.remove(module);
		moduleCnt--;
		
		if (moduleCnt==0) {
			toolbar.removeRefresh( openedFile.getGUI().getButton() );
			return true;
		}
		return false;
	}

	public void setOpenedFile(OpenedFile openedFile) {
		this.openedFile = openedFile;
	}

	public OpenedFile getOpenedFile() {
		return openedFile;
	}

	public int getModuleCnt() {
		return moduleCnt;
	}

//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//		System.out.println("Removing open file counter");
//	}
}
