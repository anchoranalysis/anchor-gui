package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.stack.DisplayStack;

@FunctionalInterface
public interface IImageStackCntrFromName {
	
	FunctionWithException<Integer,DisplayStack,GetOperationFailedException> imageStackCntrFromName( String name ) throws GetOperationFailedException;
}
