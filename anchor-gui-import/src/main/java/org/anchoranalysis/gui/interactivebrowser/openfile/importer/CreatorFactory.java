package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

/*-
 * #%L
 * anchor-gui-import
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
import java.util.function.BiConsumer;

import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.apache.commons.io.FilenameUtils;

class CreatorFactory {

	/**
	 * Creates the FileCreator from an inputManager that needs some property set (the lambda)
	 * 
	 * @param creator a new object that is a subclass of FileCreator
	 * @param propertyToSet the property-value that will be set on the FileCreator
	 * @param setPropertyFunc the lambda that sets the property 
	 * @param f file that the bean was loaded from
	 * @param customNamePrefix a prefix that is used before the custom-name
	 * @return the same object creator that is passed (after operations have been applied)
	 */
	public static <S extends FileCreator, T> FileCreator create(
		S creator,
		T propertyToSet,
		BiConsumer<S,T> setPropertyFunc,		
		File f,
		String customNamePrefix
	) {

		// Perform whatever custom setting operation is implemented by the lambda
		setPropertyFunc.accept( creator, propertyToSet );
		
		// Set the custom name
		String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
		creator.setCustomName(
			String.format("%s: %s", customNamePrefix, fileNameWithoutExt )
		);
		
		return creator;
	}

}
