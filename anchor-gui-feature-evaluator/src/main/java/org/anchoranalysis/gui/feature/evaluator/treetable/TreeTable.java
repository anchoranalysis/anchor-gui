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
package org.anchoranalysis.gui.feature.evaluator.treetable;



import javax.swing.JComponent;
import javax.swing.tree.TreeModel;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class TreeTable {

	private javax.swing.JScrollPane scrollPane;
	private org.netbeans.swing.outline.Outline outline;
	
	// renderDataProvider can be null
	public TreeTable( TreeModel treeMdl, TreeTableProperties properties ) {
		scrollPane = new javax.swing.JScrollPane();

        //Create the Outline's model, consisting of the TreeModel and the RowModel,
        //together with two optional values: a boolean for something or other,
        //and the display name for the first column:
        OutlineModel mdl = DefaultOutlineModel.createOutlineModel(
                treeMdl, properties.getRowModel(), true, properties.getTitle() );

        //Initialize the Outline object:
        outline = new Outline();
        
        scrollPane.setName("jScrollPane1"); // NOI18N

        outline.setName("outline1"); // NOI18N
        scrollPane.setViewportView(outline);
        
        if (properties.getRenderDataProvider()!=null) {
        	outline.setRenderDataProvider( properties.getRenderDataProvider() );
        }
        
        //By default, the root is shown, while here that isn't necessary:
        outline.setRootVisible(false);

        //Assign the model to the Outline object:
        outline.setModel(mdl);
        
        //Add the Outline object to the JScrollPane:
        scrollPane.setViewportView(outline);	
	}

	public void resizeColumns() {
		//int valueWidth = 100;
        outline.getColumnModel().getColumn(0).setPreferredWidth( 2000 );
        outline.getColumnModel().getColumn(1).setPreferredWidth( 1000 );
	}
	
	public JComponent getComponent() {
		return scrollPane;
	}

	public org.netbeans.swing.outline.Outline getOutline() {
		return outline;
	}
    
    
}
