/*-
 * #%L
 * anchor-gui-frame
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
package org.anchoranalysis.gui.retrieveelements;



import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.generator.raster.MIPGenerator;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

// An interface that allows receiving elements from a module
// When a function returns NULL, that element doesn't exist
public class RetrieveElementsImage extends RetrieveElements {

	private DisplayStack stack;
	
	private DisplayStack slice;
	
	public RetrieveElementsImage() {
	}

	public DisplayStack getStack() {
		return stack;
	}

	public void setStack(DisplayStack stack) {
		this.stack = stack;
	}

	public DisplayStack getSlice() {
		return slice;
	}

	public void setSlice(DisplayStack slice) {
		this.slice = slice;
	}
	
	@Override
	public void addToPopUp( AddToExportSubMenu popUp) {
		
    	if (getStack()!=null) {
    		
    		final DisplayStack currentStack = getStack();
        	
    		CachedOperation<Stack,AnchorNeverOccursException> opCreateStack = cachedOpFromDisplayStack( currentStack );
    		
    		try {
				popUp.addExportItemStackGenerator(
					"selectedStack",
					"Stack",
					opCreateStack
				);
			} catch (OperationFailedException e) {
				assert false;
			}
    		
    		if (currentStack.getDimensions().getZ() > 1) {
    			MIPGenerator generatorMIP =  new MIPGenerator(true, "selectedStackMIP");
    			
    			OperationGenerator<Stack,Stack> generator = new OperationGenerator<Stack,Stack>( generatorMIP );
    			popUp.addExportItem(
    				generator,
    				opCreateStack,
    				"selectedStackMIP",
    				"MIP",
    				generator.createManifestDescription(),
    				1
    			);
    		}
    	}
        
    	if (getSlice()!=null) {
    		
    		final DisplayStack currentSlice = getSlice();

    		try {
				popUp.addExportItemStackGenerator(
					"selectedSlice",
					"Slice",
					cachedOpFromDisplayStack(currentSlice)
				);
			} catch (OperationFailedException e) {
				assert false;
			}
    	}
	}
	
	private CachedOperation<Stack,AnchorNeverOccursException> cachedOpFromDisplayStack( DisplayStack stack ) {
		return new WrapOperationAsCached<>(
			() -> stack.createImgStack(false)
		);
	}
}
