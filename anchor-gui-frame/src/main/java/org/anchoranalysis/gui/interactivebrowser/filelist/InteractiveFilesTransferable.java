package org.anchoranalysis.gui.interactivebrowser.filelist;

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


import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.anchoranalysis.gui.file.interactive.InteractiveFile;

// Copies from web. For copying a string onto both internal and external clipboard
//  On internal clipboard it can stay as a String, on external clipboard it is changed appropriately
class InteractiveFilesTransferable implements Transferable, ClipboardOwner {
	
	@SuppressWarnings("deprecation")
	public static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;
	public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;
	public static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
	

	public static final DataFlavor[] flavors = {
	    InteractiveFilesTransferable.plainTextFlavor,
	    InteractiveFilesTransferable.localStringFlavor,
	    InteractiveFilesTransferable.fileListFlavor
	};

	private static final List<DataFlavor> flavorList = Arrays.asList( flavors );

	private InteractiveFile[] interactiveFiles;
	  
	  
	  
	  
	public InteractiveFilesTransferable(InteractiveFile[] interactiveFiles) {
		super();
		this.interactiveFiles = interactiveFiles;
	}
	
	@Override
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	
	@Override
	public boolean isDataFlavorSupported( DataFlavor flavor ) {
		return (flavorList.contains(flavor));
	}
	
	private String identiferStringFromMultiple( InteractiveFile[] files ) {
		
		boolean first = true;
		
		StringBuilder sb = new StringBuilder();
		for( InteractiveFile f : files ) {
			sb.append( f.identifier() );
			
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	private ByteArrayInputStream decideString( String str, DataFlavor flavor ) throws UnsupportedEncodingException {
		String charset = flavor.getParameter("charset").trim();
		if(charset.equalsIgnoreCase("unicode")) {
			//System.out.println("returning unicode charset");
			// uppercase U in Unicode here!
		    return new ByteArrayInputStream( str.getBytes("Unicode"));
		} else {
    	  //System.out.println("returning latin-1 charset");    
    	  return new ByteArrayInputStream( str.getBytes("iso8859-1"));
		}
	}
	
	@Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

		if (flavor.equals(InteractiveFilesTransferable.fileListFlavor)) {
			
			List<File> fileListOut = new ArrayList<>();
			for( InteractiveFile f : interactiveFiles ) {
				fileListOut.add( f.associatedFile() );
			}
			return fileListOut;
			
		} else if (flavor.equals(InteractiveFilesTransferable.plainTextFlavor)) {
			return decideString( identiferStringFromMultiple(interactiveFiles) , flavor);
	    } else if (InteractiveFilesTransferable.localStringFlavor.equals(flavor)) {
	    	return identiferStringFromMultiple(interactiveFiles);
	    } else {
	      throw new UnsupportedFlavorException(flavor);
	    }
	}
	
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {

	}
	
}