package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb;

/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> object-type
 */
public class MultiInput<T> {

	private String name;
	private NRGBackground nrgBackground;
	private Operation<T,OperationFailedException> associatedObjects;

	public MultiInput(
		String name,
		NRGBackground nrgBackground,
		Operation<T,OperationFailedException> associatedObjects
	) {
		this.name = name;
		this.nrgBackground = nrgBackground;
		this.associatedObjects = associatedObjects;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Operation<T,OperationFailedException> getAssociatedObjects() {
		return associatedObjects;
	}

	public NRGBackground getNrgBackground() {
		return nrgBackground;
	}
}
