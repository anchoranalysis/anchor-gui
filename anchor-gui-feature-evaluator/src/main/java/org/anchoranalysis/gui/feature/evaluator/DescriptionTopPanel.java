/*-
 * #%L
 * anchor-gui-feature-evaluator
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
package org.anchoranalysis.gui.feature.evaluator;


import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.gui.reassign.SimpleToggleAction;

public class DescriptionTopPanel {

	private JTextPane cfgDescriptionTop;
	
	private JPanel panelTop;
		
	private SimpleToggleAction toggleActionFreeze;
	
	public DescriptionTopPanel() {
		
		toggleActionFreeze = new SimpleToggleAction("Freeze", false);
		
		cfgDescriptionTop = new JTextPane();
		cfgDescriptionTop.setEditable(false);
		
		panelTop = new JPanel();
		panelTop.setBorder( BorderFactory.createEmptyBorder(2, 0, 2, 0) );
		panelTop.setLayout( new BorderLayout() );
		panelTop.add( new JToggleButton( toggleActionFreeze ), BorderLayout.WEST );
		panelTop.add( cfgDescriptionTop, BorderLayout.CENTER );
	}
	
	public void updateDescriptionTop( OverlayCollection overlays ) {
		
		if (overlays!=null) {
			cfgDescriptionTop.setText( "selected ids: " + new IndicesSelection( overlays.integerSet() ).toString() );
		} else {
			cfgDescriptionTop.setText("no selection");
		}
	}

	public JPanel getPanel() {
		return panelTop;
	}

	public boolean isFrozen() {
		return toggleActionFreeze.isToggleState();
	}
}
