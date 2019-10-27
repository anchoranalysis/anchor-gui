package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay;

import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;

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



import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.objmask.writer.IfElseWriter;

import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.OverlayWriter;

public class MarkDisplaySettingsWrapper implements IChangeMarkDisplaySendable {
	
	private PropertyValueChangeListenerList<MarkDisplaySettings> listListeners = new PropertyValueChangeListenerList<>();
	
	private MarkDisplaySettings markDisplaySettings;
	
	private IfElseWriter.Condition idMatchCondition;

	// Always returns false
	private static class AlwaysFalseCondition extends IfElseWriter.Condition {

		@Override
		public boolean isTrue(ObjMaskWithProperties mask, RGBStack stack, int id) {
			return false;
		}
		
	}
	
	// We assume the idMatchCondition can never be obtained
	public MarkDisplaySettingsWrapper( MarkDisplaySettings markDisplaySettings) {
		this(markDisplaySettings, new AlwaysFalseCondition() );
	}
	
	
	public MarkDisplaySettingsWrapper( MarkDisplaySettings markDisplaySettings, IfElseWriter.Condition idMatchCondition) {
		super();
		this.markDisplaySettings = markDisplaySettings;
		this.idMatchCondition = idMatchCondition;
	}

	@Override
	public void setIncludeBoundingBox(boolean b) {
		
		markDisplaySettings.setShowBoundingBox(b);
		updateMaskWriter();
	}

	@Override
	public void setShowShell(boolean showShell) {
		
		markDisplaySettings.setShowShell(showShell);
		updateMaskWriter();
	}

	@Override
	public void setShowMidpoint(boolean show) {
		markDisplaySettings.setShowMidpoint(show);
		updateMaskWriter();
	}

	@Override
	public void setShowOrientationLine(boolean show) {
		markDisplaySettings.setShowOrientationLine(show);
		updateMaskWriter();
	}

	@Override
	public void setShowThickBorder(boolean show) {
		markDisplaySettings.setShowThickBorder(show);
		updateMaskWriter();
	}
	

	@Override
	public void setShowInside(boolean show) {
		markDisplaySettings.setShowInside(show);
		updateMaskWriter();
	}

	@Override
	public void setShowSolid(boolean show) {
		markDisplaySettings.setShowSolid(show);
		updateMaskWriter();
	}
	
	private void updateMaskWriter() {
		
		for (PropertyValueChangeListener<MarkDisplaySettings> listener : listListeners ) {
			listener.propertyValueChanged( new PropertyValueChangeEvent<>(this, markDisplaySettings, false) );
		}
	}
	
	public OverlayWriter createObjMaskWriter() {
		return markDisplaySettings.createConditionalObjMaskWriter(idMatchCondition);
	}
	
	public void addChangeListener( PropertyValueChangeListener<MarkDisplaySettings> listener ) {
		listListeners.add(listener);
	}

	public void removeChangeListener( PropertyValueChangeListener<MarkDisplaySettings> listener ) {
		listListeners.remove(listener);
	}

	public MarkDisplaySettings getMarkDisplaySettings() {
		return markDisplaySettings;
	}
}
