package org.anchoranalysis.gui.videostats.operation;

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


public class VideoStatsOperationOrMenu {
	
	private boolean separator = false;

	private VideoStatsOperation operation = null;
	private VideoStatsOperationMenu menu = null;
	
	// This constructor means it's a separator
	private VideoStatsOperationOrMenu() {
		
	}
	
	public static VideoStatsOperationOrMenu createAsSeparator() {
		VideoStatsOperationOrMenu out = new VideoStatsOperationOrMenu();
		out.separator = true;
		return out;
	}
	
	public VideoStatsOperationOrMenu(VideoStatsOperationMenu menu) {
		super();
		this.menu = menu;
	}

	public VideoStatsOperationOrMenu(VideoStatsOperation operation) {
		super();
		this.operation = operation;
	}
	
	public boolean isOperation() {
		return operation!=null;
	}
	
	public boolean isMenu() {
		return menu!=null;
	}

	public VideoStatsOperation getOperation() {
		return operation;
	}

	public VideoStatsOperationMenu getMenu() {
		return menu;
	}
	
	public boolean isSeparator() {
		return separator;
	}
}