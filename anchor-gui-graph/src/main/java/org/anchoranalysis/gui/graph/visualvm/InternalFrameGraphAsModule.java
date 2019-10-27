package org.anchoranalysis.gui.graph.visualvm;

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


import javax.swing.JInternalFrame;

import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.graph.panel.ClickableGraphInstance;
import org.anchoranalysis.gui.graph.panel.GraphPanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

public class InternalFrameGraphAsModule {

	private JInternalFrame frame;
	private ClickableGraphInstance graphInstance;
	
	public InternalFrameGraphAsModule( String title, ClickableGraphInstance graphInstance ) {
		frame = new InternalFrameWithPanel( title, new GraphPanel( graphInstance ).getPanel() ).getFrame();
		this.graphInstance = graphInstance;
	}
	
	public InternalFrameGraphAsModule( String title, ClickableGraphInstance graphInstance, JInternalFrame frame ) {
		this.frame = frame;
		this.graphInstance = graphInstance;
	}
	
	public JInternalFrame getFrame() {
		return frame;
	}
		
	public IModuleCreatorDefaultState moduleCreator() {
		return defaultFrameState-> {
			VideoStatsModule module = new VideoStatsModule();
			
			module.setComponent( frame );
			
			//ISelectFrameSendable
			LinkModules link = new LinkModules(module);
			link.getFrameIndex().add(
				maybeFrameReceiver(),
				maybeFrameSendable( defaultFrameState.getLinkState().getFrameIndex() )
			);
			
			return module;
		};
	}
	
	private IPropertyValueSendable<Integer> maybeFrameSendable( int defaultIndex ) {
		IPropertyValueSendable<Integer> send = graphInstance.getSelectFrameSendable(); 
		if( send !=null ) {
			send.setPropertyValue( defaultIndex, false);
		}
		return send;
	}
	
	private IPropertyValueReceivable<Integer> maybeFrameReceiver() {
		if( graphInstance.getSelectFrameReceivable()!=null ) {
			return graphInstance.getSelectFrameReceivable();
		} else {
			return null;
		}
	}

}
