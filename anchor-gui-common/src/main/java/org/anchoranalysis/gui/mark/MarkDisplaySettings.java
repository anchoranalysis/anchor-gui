package org.anchoranalysis.gui.mark;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.io.bean.objmask.writer.IfElseWriter;
import org.anchoranalysis.io.bean.objmask.writer.NullWriter;
import org.anchoranalysis.io.bean.objmask.writer.ObjMaskListWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBBBoxOutlineWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBMidpointWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBOrientationWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.bean.objmask.writer.RGBSolidWriter;

import ch.ethz.biol.cell.imageprocessing.io.objmask.ObjMaskWriter;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.SimpleOverlayWriter;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

// Contains display settings for a mark
public class MarkDisplaySettings {

	private boolean showBoundingBox = false;
	
	private boolean showShell = false;
	
	private boolean showInside = true;
	
	private boolean showMidpoint = false;

	private boolean showOrientationLine = false;
	
	private boolean showThickBorder = false;
	
	private boolean showSolid = false;
	
	public OverlayWriter createConditionalObjMaskWriter( IfElseWriter.Condition conditionSelected ) {
		
		int borderSize = showThickBorder ? 6 : 1;
		
		List<ObjMaskWriter> insideList = new ArrayList<>();
		List<ObjMaskWriter> shellList = new ArrayList<>();
		
		if (showInside) {
			addShowInside( insideList, conditionSelected, borderSize );
		}

		if (showBoundingBox) {
			insideList.add( new RGBBBoxOutlineWriter(borderSize) );
		}
		
		if (showShell) {
			addShowShell(insideList, shellList, borderSize );
		}
		
		return determineWriter( insideList, shellList );

	}
	
	public MarkDisplaySettings duplicate() {
		MarkDisplaySettings copy = new MarkDisplaySettings();
		copy.showBoundingBox = this.showBoundingBox;
		copy.showShell = this.showShell;
		copy.showInside = this.showInside;
		copy.showMidpoint = this.showMidpoint;
		copy.showOrientationLine = this.showOrientationLine;
		copy.showThickBorder = this.showThickBorder;
		copy.showSolid = this.showSolid;
		return copy;
	}

	private ObjMaskWriter createInsideConditionalWriter( IfElseWriter.Condition conditionSelected, int borderSize ) {
		
		// TRUE WRITER is for when selected
		ObjMaskWriter trueWriter = new RGBSolidWriter();
		
		// FALSE writer is for when not selected
		RGBOutlineWriter falseWriter = new RGBOutlineWriter(borderSize);
		falseWriter.setForce2D(true);
		
		// Combining both situations gives us a selectable
		ObjMaskWriter edgeSelectableWriter = new IfElseWriter(conditionSelected, trueWriter, falseWriter);
		
		return edgeSelectableWriter;
	}
	
	// We don't bother with an ObjMaskListWriter if there's a single item - to avoid minor overhead
	private static ObjMaskWriter createWriterFromList( List<ObjMaskWriter> writerList ) {
		
		if (writerList.size()==0) {
			return null;
		}
		
		return writerList.size() > 1 ? new ObjMaskListWriter(writerList) : writerList.get(0);
	}
			
	private void addShowInside( List<ObjMaskWriter> insideList, IfElseWriter.Condition conditionSelected, int borderSize ) {
		insideList.add( createInsideConditionalWriter(conditionSelected,borderSize) );
		
		if (showSolid) {
			insideList.add( new RGBSolidWriter() );
		} else {
		
			// We only consider these if we are not considering a solid
			if (showMidpoint) {
				insideList.add( new RGBMidpointWriter() );
			}
			
			if (showOrientationLine) {
				insideList.add( new RGBOrientationWriter() );
			}
		}
	}
	

	private void addShowShell( List<ObjMaskWriter> insideList, List<ObjMaskWriter> shellList, int borderSize ) {

		RGBOutlineWriter outlineWriter = new RGBOutlineWriter(borderSize);
		outlineWriter.setForce2D(true);
		shellList.add( outlineWriter );
		
		if (showBoundingBox) {
			shellList.add( new RGBBBoxOutlineWriter(borderSize) );
		}
		
		// If showInside is switched off, then we have a second chance to show the midpoint
		if (showMidpoint && !showInside) {
			shellList.add( new RGBMidpointWriter() );
		}
		
		if (showOrientationLine && !showInside) {
			insideList.add( new RGBOrientationWriter() );
		}		
	}
	
	private static SimpleOverlayWriter determineWriter( List<ObjMaskWriter> insideList, List<ObjMaskWriter> shellList ) {

		ObjMaskWriter insideWriter = createWriterFromList(insideList);
		ObjMaskWriter shellWriter = createWriterFromList(shellList);
		
		RegionMembershipWithFlags inside = RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
		RegionMembershipWithFlags shell = RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_SHELL);
				
		if (shellList.size()>0) {
			return new SimpleOverlayWriter(	shellWriter, shell );
		} else {
			
			if (insideList.size()>0) {
				return new SimpleOverlayWriter(	insideWriter, inside );
			} else {
				// Then there is no mask
				// We should not get here at the moment, as it is impossible to disable showInside
				return new SimpleOverlayWriter(	new NullWriter(), inside );
			}
		}
	}

	public boolean isShowMidpoint() {
		return showMidpoint;
	}

	public void setShowMidpoint(boolean showMidpoint) {
		this.showMidpoint = showMidpoint;
	}

	public boolean isShowOrientationLine() {
		return showOrientationLine;
	}

	public void setShowOrientationLine(boolean showOrientationLine) {
		this.showOrientationLine = showOrientationLine;
	}

	public boolean isShowThickBorder() {
		return showThickBorder;
	}

	public void setShowThickBorder(boolean showThickBorder) {
		this.showThickBorder = showThickBorder;
	}

	public boolean isShowSolid() {
		return showSolid;
	}

	public void setShowSolid(boolean showSolid) {
		this.showSolid = showSolid;
	}

	public boolean isShowInside() {
		return showInside;
	}

	public void setShowInside(boolean showInside) {
		this.showInside = showInside;
	}
	
	
	public boolean isShowBoundingBox() {
		return showBoundingBox;
	}

	public void setShowBoundingBox(boolean showBoundingBox) {
		this.showBoundingBox = showBoundingBox;
	}

	public boolean isShowShell() {
		return showShell;
	}

	public void setShowShell(boolean showShell) {
		this.showShell = showShell;
	}
}
