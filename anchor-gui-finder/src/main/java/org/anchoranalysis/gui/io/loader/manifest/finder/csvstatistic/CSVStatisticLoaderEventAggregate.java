/*-
 * #%L
 * anchor-gui-finder
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
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;



import java.nio.file.Path;

import org.anchoranalysis.core.index.container.ArrayListContainer;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public class CSVStatisticLoaderEventAggregate extends CSVStatisticLoader {

	@Override
	public BoundedIndexContainer<CSVStatistic> createContainerFromCSV(	Path csvPath ) throws CSVReaderException {

		ArrayListContainer<CSVStatistic> cntr = new ArrayListContainer<>();
		
		try( ReadByLine reader = CSVReaderByLine.open(csvPath)) {
			reader.read(
				(line, firstLine) -> {
					int i = 0;
					
					CSVStatistic stat = new CSVStatistic();
					stat.setIter( Integer.parseInt(line[i++]) );
					stat.setSize( Integer.parseInt(line[i++]) );
					stat.setNrg( Double.parseDouble(line[i++]) );
					cntr.add( stat );
				}
			);
		}
		return cntr;
	}

}
