package org.anchoranalysis.gui.videostats.internalframe;

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


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalRerouteErrors;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalWriter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

import ch.ethz.biol.cell.imageprocessing.io.generator.raster.CfgGenerator;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.CfgWithDisplayStack;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.ColoredCfgWithDisplayStack;
import ch.ethz.biol.cell.imageprocessing.io.idgetter.IDGetterMarkID;

public class OutputPanel {

	private JPanel panel;
	
	private BoundOutputManagerRouteErrors outputManager;
	
	private GeneratorSequenceIncrementalRerouteErrors<ColoredCfgWithDisplayStack> sequenceWriter;
	
	private ColorIndex colorIndex;
	
	private final ErrorReporter errorReporter;
	
	private class StartAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6082901454998007224L;

		public StartAction() {
			super("Start Sequence");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			CfgGenerator generator = new CfgGenerator( new RGBOutlineWriter(2), new IDGetterOverlayID() );
			
			IndexableOutputNameStyle outputNameStyle = new IntegerSuffixOutputNameStyle("markEvaluator",6);
			
			sequenceWriter = new GeneratorSequenceIncrementalRerouteErrors<>( 
				new GeneratorSequenceIncrementalWriter<>( 
					outputManager.getDelegate(),
					outputNameStyle.getOutputName(),
					outputNameStyle,
					generator,
					new ManifestDescription("raster", "markEvaluator"),
					0,
					true
				),
				errorReporter
			);
			
			sequenceWriter.start();
		
		}
		
	}
	
	
	private class EndAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1812205932710878624L;

		public EndAction() {
			super("End Sequence");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (sequenceWriter!=null) {
				sequenceWriter.end();
				sequenceWriter = null;
			}
		}
		
	}
	
	
	
	public OutputPanel( ErrorReporter errorReporter) {
		
		panel = new JPanel();
		panel.setLayout( new FlowLayout() );
	
		{
			JButton button = new JButton( new StartAction() );
			panel.add(button);
		}
		{
			JButton button = new JButton( new EndAction() );
			panel.add(button);
		}
		
		this.errorReporter = errorReporter;
	}
	
	public void init( ColorIndex colorIndex, BoundOutputManagerRouteErrors outputManager ) {
		this.colorIndex = colorIndex;
		this.outputManager = outputManager;
	}

	public JPanel getPanel() {
		return panel;
	}

	public void output( CfgWithDisplayStack cws ) {
		if (sequenceWriter!=null) {
			
			ColoredCfgWithDisplayStack colored = new ColoredCfgWithDisplayStack(cws,colorIndex,new IDGetterMarkID() );
			sequenceWriter.add(colored);
		}
	}
}
