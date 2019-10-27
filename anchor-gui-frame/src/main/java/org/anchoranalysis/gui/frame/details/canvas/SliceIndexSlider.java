package org.anchoranalysis.gui.frame.details.canvas;

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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.anchoranalysis.core.index.container.IBoundedRange;
import org.anchoranalysis.core.index.container.IBoundedRangeIncompleteDynamic;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.gui.image.IndexSlider;
import org.anchoranalysis.gui.slider.SideSlider;

class SliceIndexSlider {

	private JPanel delegate;
	
	private SideSlider stackSlider;
	private IndexSlider indexSlider;

	public SliceIndexSlider( IBoundedRange sliceBounds, IBoundedRangeIncompleteDynamic indexBounds, boolean includeAdjustingIndex ) {
		
		delegate = new JPanel();
		delegate.setLayout( new BorderLayout() );
		delegate.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		// Stack slider
		stackSlider = new SideSlider( sliceBounds.getMinimumIndex(), sliceBounds.getMaximumIndex(), 1, 3, true);
		stackSlider.setVisible(true);
		
		delegate.add( stackSlider, BorderLayout.NORTH );
	
		indexSlider = new IndexSlider( indexBounds, includeAdjustingIndex );
		indexSlider.setVisible(true);
		
		delegate.add( indexSlider.getSlider(), BorderLayout.SOUTH );
		
		updateSliderVisibilityFromBounds(sliceBounds);
	}
	
	public void dispose() {
		stackSlider.dispose();
		indexSlider.dispose();
	}
	
	public void setSliceBounds( IBoundedRange sliceBounds ) {
		stackSlider.setMinimum( sliceBounds.getMinimumIndex() );
		stackSlider.setMaximum( sliceBounds.getMaximumIndex() );
		updateSliderVisibilityFromBounds( sliceBounds );
	}
	
	private void setStackSliderVisible( boolean visibility ) {
		stackSlider.setVisible(visibility);
	}
	
	public void updateSliderVisibilityFromBounds( IBoundedRange sliceBounds ) {
		setStackSliderVisible( sliceBounds.getMinimumIndex()!=sliceBounds.getMaximumIndex() );
	}
	
	public void setIndexSliderVisible( boolean visibility ) {
		indexSlider.setVisible(visibility);
	}
	
	public boolean getIndexSliderVisible() {
		return indexSlider.isVisible();
	}
	
	public boolean getStackSliderVisible() {
		return stackSlider.isVisible();
	}
	
	public JComponent getComponent() {
		return delegate;
	}
	
	public IPropertyValueReceivable<Integer> getSelectSliceReceivable() {
		return stackSlider.getSelectSliceReceivable();
	}
	
	public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
		return indexSlider.getSelectIndexReceivable();
	}
	
	public int getSliceNum() {
		return stackSlider.getValue();
	}
	
	public int getIndex() {
		return indexSlider.getIndex();
	}

	public void setIndex( int index, boolean adjusting ) {
		indexSlider.getSelectIndexSendable().setPropertyValue(index, adjusting);
	}
	
	public void setSliceNum(int sliceNum) {
		stackSlider.setValue(sliceNum,false);
	}
	
	public void setIndexToMaximum() {
		indexSlider.setIndexToMaximum();
	}
	
	public void setSliceNumToCenter() {
		stackSlider.setValueToCenter();
	}
	
	public boolean isIndexAtMaximum() {
		return indexSlider.isIndexAtMaximum();
	}
	

	
	public void setIndexMaximum( int max ) {
		indexSlider.setIndexMaximum(max);
	}
	
	public void setIndexMinimum( int min ) {
		indexSlider.setIndexMinimum(min);
	}

	public void incrementStack() {
		stackSlider.increment();
	}

	public void decrementStack() {
		stackSlider.decrement();
	}

//	public Dimension getPreferredSize() {
//		Dimension d = delegate.getPreferredSize();
//		
//		return new Dimension( (int) d.getWidth()-13, (int) d.getHeight()-13 );
//	}


}
