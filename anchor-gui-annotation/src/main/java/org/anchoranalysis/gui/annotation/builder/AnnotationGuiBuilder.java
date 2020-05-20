package org.anchoranalysis.gui.annotation.builder;

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

import java.io.File;
import java.nio.file.Path;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.CachedOperationWrap;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.image.stack.Stack;

/** Builds the GUI components to support a strategy */
public abstract class AnnotationGuiBuilder<T extends AnnotationInitParams> {
		
	private CachedOperation<AnnotationSummary,CreateException> queryAnnotationStatus;
	
	public AnnotationGuiBuilder() {
		super();
		this.queryAnnotationStatus = createCachedSummary();
	}
	
	/** Should the user be prompted to use a default annotation? */
	public abstract boolean isUseDefaultPromptNeeded();
	
	public abstract T createInitParams(
		ProgressReporterMultiple prm,
		AnnotationGuiContext context,
		LogErrorReporter logErrorReporter,
		boolean useDefaultCfg
	) throws CreateException;
	
	/**
	 * A mechanism for exporting annotations
	 * 
	 * @return mechanism or NULL if it isn't supported
	 */
	public abstract ExportAnnotation exportAnnotation();
	
	public abstract PanelNavigation createInitPanelNavigation(
		T paramsInit,
		AnnotationPanelParams params,
		AnnotationFrameControllers controllers
	);
	
	public abstract void showAllAdditional(
		T paramsInit,
		AdditionalFramesContext context
	) throws OperationFailedException;
	

	/** A cached annotation-summary for the annotation
	 * 
	 * The cache should be reset of the annotation is changed by the user
	 **/
	public CachedOperation<AnnotationSummary,CreateException> queryAnnotationSummary() {
		return queryAnnotationStatus;
	}
	
	public abstract ChangeableBackgroundDefinition backgroundDefinition( AnnotationBackground annotationBackground );
	
	/** A path that is used to allow the user to delete an annotation */
	public abstract Path deletePath();
		
	// Cached-operation
	public abstract OperationWithProgressReporter<
		INamedProvider<Stack>,
		CreateException
	> stacks();
	
	public abstract String descriptiveName();
	
	public abstract File associatedFile();
	
	/** Height in pixels of the panel that isn't the 'image' section of the frame */
	public abstract int heightNonImagePanel();
	
	/** Creates an annotation summary for the current-annotation */
	protected abstract AnnotationSummary createSummary() throws CreateException;
	
	/** Creates a cached version of the createSummary() method */
	private CachedOperation<AnnotationSummary,CreateException> createCachedSummary() {
		return new CachedOperationWrap<>(
			() -> createSummary()
		);
	}
}
