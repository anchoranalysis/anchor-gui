package org.anchoranalysis.gui.feature.evaluator.treetable;

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


import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.netbeans.swing.outline.Outline;

import ch.ethz.biol.cell.mpp.pair.Pair;

public interface ITreeTableModel extends IUpdatableSinglePair {

	void resizeColumns();
	
	@Override
	void updateSingle(Overlay overlay, NRGStackWithParams raster);

	void updatePair(Pair<Overlay> pair, NRGStackWithParams raster);
	
	// We should do this to all our tables
	void addMouseListenerToOutline( MouseListener ml );
	
	JComponent getComponent();
	
	Outline getOutline();
}
