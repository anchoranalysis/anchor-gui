package org.anchoranalysis.gui.io.loader.manifest.finder;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderDeserializer;
import org.anchoranalysis.io.manifest.finder.FinderSingleFolder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FolderWriteAnd;
import org.anchoranalysis.io.manifest.match.FolderWritePath;
import org.anchoranalysis.io.manifest.match.helper.folderwrite.FolderWriteFileFunctionType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

public class FinderCfgFolder extends FinderSingleFolder {

	private String manifestFunction;
	
	private Deserializer<Cfg> deserializer = new XStreamDeserializer<>();
	
	private String folderName;
	
	
	public FinderCfgFolder(String folderName, String manifestFunction) {
		super();
		this.manifestFunction = manifestFunction;
		this.folderName = folderName;
	}

	@Override
	protected FolderWrite findFolder(ManifestRecorder manifestRecorder) {

		FolderWriteAnd folderAdd = new FolderWriteAnd();
		folderAdd.addCondition( new FolderWritePath(folderName) );
		folderAdd.addCondition( new FolderWriteFileFunctionType( this.manifestFunction, "serialized" ) );
		
		List<FolderWrite> list = FinderUtilities.findListFolder(manifestRecorder, folderAdd );
		
		if (list.size()>0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	private static class OperationCreateCfg extends CachedOperation<Cfg,OperationFailedException>  {

		private SequencedFolderDeserializer<Cfg> sfrr;
		private int index;
		
		public OperationCreateCfg(SequencedFolderDeserializer<Cfg> sfrr, int index) {
			super();
			this.sfrr = sfrr;
			this.index = index;
		}

		@Override
		protected Cfg execute() throws OperationFailedException {
			try {
				return sfrr.get(index);
			} catch (GetOperationFailedException e) {
				throw new OperationFailedException(e);
			}
		}
		
	}
	
	// If namesAsIndexes is true, we use the indexes as names instead of the existing names
	public INamedProvider<Cfg> createNamedProvider( boolean namesAsIndexes, LogErrorReporter logErrorReporter ) throws OperationFailedException {
		
		if (getFoundFolder()==null) {
			return new NameValueSet<>();
		}
		
		LazyEvaluationStore<Cfg> out = new LazyEvaluationStore<>(logErrorReporter, "finderCfgFolder");
		
		SequencedFolderDeserializer<Cfg> sfrr = new SequencedFolderDeserializer<>(getFoundFolder(),deserializer);
		
		SequenceType st = getFoundFolder().getAssociatedSequence(); 
		int min = st.getMinimumIndex();
			
		for (int i=min; i!=-1; i=st.nextIndex(i) ) {
			String name = st.indexStr(i);

			if (namesAsIndexes) {
				out.add( String.valueOf(i), new OperationCreateCfg(sfrr, i));
			} else {
				out.add( name, new OperationCreateCfg(sfrr, i) );
			}
		}
		return out;
	}
}
