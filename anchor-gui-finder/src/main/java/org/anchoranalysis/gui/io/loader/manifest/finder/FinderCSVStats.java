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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatisticLoader;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatisticLoaderEventAggregate;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatisticLoaderIntervalAggregate;
import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleFile;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.FileWriteAnd;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchOr;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;

public class FinderCSVStats extends FinderSingleFile {

	private String outputName;
	
	private IBoundedIndexContainer<CSVStatistic> statsCntr;
	
	public FinderCSVStats(String outputName, ErrorReporter errorReporter) {
		super(errorReporter);
		this.outputName = outputName;
	}

	private static List<FileWrite> findIncrementalCSVStats( ManifestRecorder manifestRecorder, String type, String outputName ) throws MultipleFilesException {
		
		FileWriteAnd match = new FileWriteAnd();
		
		match.addCondition( new FileWriteManifestMatch(
				new ManifestDescriptionMatchAnd(
						new ManifestDescriptionMatchOr(
								new ManifestDescriptionFunctionMatch("event_aggregate_stats"),
								new ManifestDescriptionFunctionMatch("interval_aggregate_stats")
						), 
						new ManifestDescriptionTypeMatch(type)
				)
		) );
		
		match.addCondition( new FileWriteOutputName( outputName ) );
		
		ArrayList<FileWrite> foundList = new ArrayList<>();
		manifestRecorder.getRootFolder().findFile(foundList, match, false);
		
		return foundList;
	}


	private IBoundedIndexContainer<CSVStatistic> createCtnr( FileWrite fileWrite )
			throws GetOperationFailedException {
		
		
		try {
			CSVStatisticLoader loader = null;
			if (fileWrite.getManifestDescription().getFunction().equals("event_aggregate_stats")) {
				loader = new CSVStatisticLoaderEventAggregate();
			} else if (fileWrite.getManifestDescription().getFunction().equals("interval_aggregate_stats")) {
				loader = new CSVStatisticLoaderIntervalAggregate();
			}
			return loader.createContainerFromCSV(fileWrite.calcPath() );
			
		} catch (CSVReaderException e) {
			throw new GetOperationFailedException(e);
		}
	}

	public IBoundedIndexContainer<CSVStatistic> get() throws GetOperationFailedException {
		
		assert( exists() );
		
		if (statsCntr==null) {
			statsCntr = createCtnr( getFoundFile() );
		}
		return statsCntr;
	}

	@Override
	protected FileWrite findFile(ManifestRecorder manifestRecorder)
			throws MultipleFilesException {
		
		if (outputName.isEmpty()) {
			return null; 
		}
		
		List<FileWrite> found = findIncrementalCSVStats( manifestRecorder, "csv", outputName );
		
		if (found.size()>=2) {
			throw new MultipleFilesException("Multiple matching manifest descriptions find");
		}
		
		if (found.size()==0) {
			return null;
		}
		return found.get(0);
	}
}
