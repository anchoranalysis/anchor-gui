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

package org.anchoranalysis.gui.finder.csvstatistic;

import java.nio.file.Path;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.io.input.csv.CSVReaderByLine;
import org.anchoranalysis.io.input.csv.CSVReaderException;
import org.anchoranalysis.io.input.csv.ReadByLine;

public class CSVStatisticLoaderIntervalAggregate extends CSVStatisticLoader {

    @Override
    public BoundedIndexContainer<CSVStatistic> createContainerFromCSV(Path csvPath)
            throws CSVReaderException {

        ArrayListContainer<CSVStatistic> cntr = new ArrayListContainer<>();

        try (ReadByLine reader = CSVReaderByLine.open(csvPath)) {

            int numKernels = CountKernelsInCSV.apply(reader.headers());

            reader.read((line, firstLine) -> cntr.add(statFromLine(line, cntr, numKernels)));
        }
        return cntr;
    }

    private static CSVStatistic statFromLine(
            String line[], ArrayListContainer<CSVStatistic> cntr, int numKernels) {

        int i = 0;

        CSVStatistic stat = new CSVStatistic();
        stat.setIter(Integer.parseInt(line[i++]));
        stat.setEnergy(Double.parseDouble(line[i++]));
        stat.setSize(Double.parseDouble(line[i++]));
        // stat.setAccptProb( Double.parseDouble(line[i++]) );
        stat.setAccptProbAll(Double.parseDouble(line[i++]));
        // stat.setAccptProbRand( Double.parseDouble(line[i++]) );
        stat.setTemperature(Double.parseDouble(line[i++]));

        // We could read the header names instead, rather than relying on kernel proposer
        //   to determine how many kernels there are
        if (numKernels > 0) {
            stat.setKernelProp(readDoubleArrKernelFactories(stat, numKernels, line, i));
            i += stat.getKernelProp().length;

            stat.setKernelAccpt(readDoubleArrKernelFactories(stat, numKernels, line, i));
            i += stat.getKernelAccpt().length;
        }

        stat.setTime(Double.parseDouble(line[i++]));
        stat.setTimePerIter(Double.parseDouble(line[i++]));
        stat.setIntervalTimePerIter(Double.parseDouble(line[i++]));

        return stat;
    }

    private static double[] readDoubleArrKernelFactories(
            CSVStatistic stat, int numKernels, String[] line, int startingIndex) {

        double arr[] = new double[numKernels];
        for (int i = 0; i < numKernels; i++) {
            arr[i] = Double.parseDouble(line[startingIndex++]);
        }
        return arr;
    }
}
