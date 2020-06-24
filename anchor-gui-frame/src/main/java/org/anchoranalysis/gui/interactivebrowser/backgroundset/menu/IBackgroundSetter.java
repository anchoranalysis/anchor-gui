package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.stack.DisplayStack;

@FunctionalInterface
public interface IBackgroundSetter {
	
	void setImageStackCntr(
		FunctionWithException<Integer,DisplayStack,GetOperationFailedException> imageStackCntr
	) throws SetOperationFailedException;
}
