package org.anchoranalysis.gui.retrieveelements;

import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public interface IAddToExportSubMenu {

	void addExportItemStackGenerator( String outputName, String label, Operation<Stack,AnchorNeverOccursException> stack ) throws OperationFailedException;
	
	<T> void addExportItem( IterableGenerator<T> generator, T itemToGenerate, String outputName, String label, ManifestDescription md, int numItems );
	
	BoundOutputManagerRouteErrors getOutputManager();
}
