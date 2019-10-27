package org.anchoranalysis.gui.interactivebrowser.openfile.type;

/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import ch.ethz.biol.cell.mpp.nrg.nrgscheme.creator.NRGSchemeCreator;

/** Stores a single instance of a NrgSchemeCreator that is used when creating XML beans (e.g. loading FeatureLists)
 *  SINGLETON
 *  
 *  Originally, it is set to NULL and can be updated afterwards
 * */
public class NrgSchemeCreatorState {

	private static NrgSchemeCreatorState instance;
	
	private NRGSchemeCreator item;

	private NrgSchemeCreatorState() {
		
	}
	
	public static NrgSchemeCreatorState instance() {
		if (instance==null) {
			instance = new NrgSchemeCreatorState();
		}
		return instance;
	}
	
	/** Can return NULL */
	public NRGSchemeCreator getItem() {
		return item;
	}

	public void setItem(NRGSchemeCreator item) {
		this.item = item;
	}
}
