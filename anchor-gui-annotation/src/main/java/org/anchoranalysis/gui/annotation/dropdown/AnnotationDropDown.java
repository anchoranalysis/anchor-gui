package org.anchoranalysis.gui.annotation.dropdown;

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

import javax.swing.JFrame;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.file.opened.IOpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSetWithAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.DropDownUtilitiesRaster;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromCreatorAndAdder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class AnnotationDropDown {
	
	private BoundVideoStatsModuleDropDown delegate;
	private String name;
	private AnnotationGuiBuilder<?> annotation;
	private AnnotationGuiContext context;
	
	// A dropdown menu representing a particular manifest
	public AnnotationDropDown(
		AnnotationGuiBuilder<?> annotation,
		AnnotationGuiContext context,
		String name	)
	{
		this.delegate = new BoundVideoStatsModuleDropDown( name, "/toolbarIcon/rectangle.png");
		this.name = name;
		this.annotation = annotation;
		this.context = context;
	}
	
	public void init(
		IAddVideoStatsModule adder,
		BoundOutputManagerRouteErrors outputManager,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		
		addAnnotation(
			adder,
			outputManager.getOutputWriteSettings(),
			mpg
		);
				
		// Add Channel Viewer
		addChannelViewer( adder, mpg );
				
		delegate.getRootMenu().addSeparator();
		
		addDelete( adder.getParentFrame() );
		addExport( adder.getParentFrame() );
	}
	
	private void addAnnotation(
		IAddVideoStatsModule adder,			
		OutputWriteSettings ows,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		String desc = String.format("Annotator: %s",name);
		
		VideoStatsModuleCreator moduleCreator = new AnnotatorModuleCreator<>(
			desc,
			annotation,
			context,
			ows,
			mpg
		); 	
		
		VideoStatsModuleCreatorAndAdder creatorAndAdder = new VideoStatsModuleCreatorAndAdder(
			new IdentityOperationWithProgressReporter<>(adder),
			moduleCreator
		); 
		delegate.getRootMenu().addAsDefault(
			new VideoStatsOperationFromCreatorAndAdder("Annotator", creatorAndAdder, mpg.getThreadPool(), mpg.getLogErrorReporter() )
		);
	}
	
	private void addDelete( JFrame frame ) {
		delegate.getRootMenu().add(
			new DeleteAnnotationOperation(
				frame,
				annotation.deletePath(),
				context.getAnnotationRefresher()
			)
		);
	}
	
	private void addExport( JFrame frame ) {
		ExportAnnotation ea = annotation.exportAnnotation();
		if (ea!=null) {
			delegate.getRootMenu().add(
				new ExportAnnotationOperation(
					frame,
					ea,
					context.getAnnotationRefresher()
				)
			);
		}
	}
	
	private void addChannelViewer( IAddVideoStatsModule adder, VideoStatsModuleGlobalParams mpg ) {
		OperationCreateBackgroundSetWithAdder operationBwsa = new OperationCreateBackgroundSetWithAdder(
			NRGBackground.createStack(
				annotation.stacks(),
				null
			),
			adder,
			mpg.getThreadPool(),
			mpg.getLogErrorReporter().errorReporter()
		);
		
		DropDownUtilitiesRaster.addRaster(
			delegate.getRootMenu(),
			delegate,
			operationBwsa.nrgBackground(),
			"Channel Viewer",
			mpg,
			false
		);
	}
	
	public IOpenedFileGUI openedFileGUI() {
		return delegate.openedFileGUI();
	}

	public String getName() {
		return delegate.getName();
	}
}
