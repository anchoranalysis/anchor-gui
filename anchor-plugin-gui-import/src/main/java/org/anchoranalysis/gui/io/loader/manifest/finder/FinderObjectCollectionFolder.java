package org.anchoranalysis.gui.io.loader.manifest.finder;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWritePath;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FinderObjectCollectionFolder extends FinderSingleFolder {

	private final String folderName;

	@Override
	protected Optional<FolderWrite> findFolder(ManifestRecorder manifestRecorder) {
		
		List<FolderWrite> list = FinderUtilities.findListFolder(
			manifestRecorder,
			new FolderWritePath(folderName)
		);
		
		if (!list.isEmpty()) {
			return Optional.of( list.get(0) );
		} else {
			return Optional.empty();
		}
	}
	
	// If namesAsIndexes is true, we use the indexes as names instead of the existing names
	public NamedProvider<ObjectCollection> createNamedProvider(Logger logger) throws OperationFailedException {
		
		if (getFoundFolder()==null) {
			return new NameValueSet<>();
		}
		
		return new CreateObjectStoreFromDirectory().apply(
			getFoundFolder().calcPath(),
			logger
		);
	}
}