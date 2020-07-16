/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

import java.nio.file.Path;
import org.anchoranalysis.core.index.container.ArrayListContainer;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public class CSVStatisticLoaderEventAggregate extends CSVStatisticLoader {

    @Override
    public BoundedIndexContainer<CSVStatistic> createContainerFromCSV(Path csvPath)
            throws CSVReaderException {

        ArrayListContainer<CSVStatistic> cntr = new ArrayListContainer<>();

        try (ReadByLine reader = CSVReaderByLine.open(csvPath)) {
            reader.read(
                    (line, firstLine) -> {
                        int i = 0;

                        CSVStatistic stat = new CSVStatistic();
                        stat.setIter(Integer.parseInt(line[i++]));
                        stat.setSize(Integer.parseInt(line[i++]));
                        stat.setNrg(Double.parseDouble(line[i++]));
                        cntr.add(stat);
                    });
        }
        return cntr;
    }
}
