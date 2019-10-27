package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

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


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class PanelWithLabel {

	private JPanel delegate = new JPanel();
	private JLabel label; 
		
	public void init( String labelText ) {
		delegate.setLayout( new GridBagLayout() );
		
		this.label = new JLabel(labelText,JLabel.CENTER);
		
		addComponent( label, 0, GridBagConstraints.HORIZONTAL );
		addComponent( createMainPanel(), 1, GridBagConstraints.BOTH );
	}
	
	
	protected abstract JPanel createMainPanel();
	
	public JComponent getPanel() {
		return delegate;
	}
	
	public void setLabelText( String text ) {
		label.setText(text);
	}


	public void setLabelForeground(Color fg) {
		label.setForeground(fg);
	}
	
	private void addComponent( JComponent componentToAdd, int gridy, int fill ) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = gridy;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		delegate.add( componentToAdd, c );
	}
}
