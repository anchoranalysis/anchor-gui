/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

import java.nio.file.Path;
import org.anchoranalysis.core.index.container.ArrayListContainer;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public class CSVStatisticLoaderIntervalAggregate extends CSVStatisticLoader {

    @Override
    public BoundedIndexContainer<CSVStatistic> createContainerFromCSV(Path csvPath)
            throws CSVReaderException {

        ArrayListContainer<CSVStatistic> cntr = new ArrayListContainer<>();

        try (ReadByLine reader = CSVReaderByLine.open(csvPath)) {

            int numKernels = CntKernelsFromCsv.apply(reader.headers());

            reader.read((line, firstLine) -> cntr.add(statFromLine(line, cntr, numKernels)));
        }
        return cntr;
    }

    private static CSVStatistic statFromLine(
            String line[], ArrayListContainer<CSVStatistic> cntr, int numKernels) {

        int i = 0;

        CSVStatistic stat = new CSVStatistic();
        stat.setIter(Integer.parseInt(line[i++]));
        stat.setNrg(Double.parseDouble(line[i++]));
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
