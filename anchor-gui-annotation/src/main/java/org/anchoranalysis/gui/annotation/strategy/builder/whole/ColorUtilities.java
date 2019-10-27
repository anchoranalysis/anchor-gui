package org.anchoranalysis.gui.annotation.strategy.builder.whole;

/*-
 * #%L
 * anchor-gui-annotation
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

import java.awt.Color;

import javax.swing.JButton;

import org.anchoranalysis.io.bean.color.RGBColorBean;

class ColorUtilities {
	
	public static void maybeAddColor( JButton button, RGBColorBean color) {
		if (color!=null) {
			addColor( button, color.toAWTColor() );
		}
	}
	
	public static void addColor( JButton button, Color col ) {
		button.setBackground( col );
		button.setForeground( getContrastColor(col) );		
	}
	
	/**
	 * Calculates a contrast color (black or white) for a particular color. Useful for text labels
	//  on a particular background
	 * 
	 * Inspired by:
	 * 
	 * https://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
	 * 
	 * @param col input color
	 * @return the contrasted color
	 */
	private static Color getContrastColor(Color col) {
	    double lum = (((0.299 * col.getRed()) + ((0.587 * col.getGreen()) + (0.114 * col.getBlue()))));
	    return lum > 186 ? Color.BLACK: Color.WHITE;
	}
}
