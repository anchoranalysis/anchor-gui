package org.anchoranalysis.gui.annotation;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSet;
import org.anchoranalysis.gui.videostats.link.DefaultLinkStateManager;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

/** The rasters which form the background to the annotations */
public class AnnotationBackground {

	private OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> backgroundSetOp;
	private FunctionWithException<Integer,DisplayStack,GetOperationFailedException> defaultBackground;
	private ImageDimensions dimViewer;
	
	public AnnotationBackground(
		ProgressReporterMultiple prm,
		NamedProvider<Stack> backgroundStacks,
		String stackNameVisualOriginal
	) throws GetOperationFailedException {
		backgroundSetOp = new OperationCreateBackgroundSet(backgroundStacks);
		{
			defaultBackground = backgroundSetOp.doOperation(
				new ProgressReporterOneOfMany(prm)
			).stackCntr(stackNameVisualOriginal);
			
			if (defaultBackground==null) {
				throw new GetOperationFailedException(
					String.format("Cannot find stackName '%s'", stackNameVisualOriginal )
				);
			}
			
			dimViewer = defaultBackground.apply(0).getDimensions();
		}		
	}
		
	public void configureLinkManager( DefaultLinkStateManager linkStateManager ) {
		linkStateManager.setBackground( defaultBackground );
		linkStateManager.setSliceNum( dimViewer.getZ()/2 );		
	}

	public ImageDimensions getDimensionsViewer() {
		return dimViewer;
	}

	public FunctionWithException<Integer,DisplayStack,GetOperationFailedException> getDefaultBackground() {
		return defaultBackground;
	}

	public OperationWithProgressReporter<BackgroundSet,GetOperationFailedException> getBackgroundSetOp() {
		return backgroundSetOp;
	}
}
