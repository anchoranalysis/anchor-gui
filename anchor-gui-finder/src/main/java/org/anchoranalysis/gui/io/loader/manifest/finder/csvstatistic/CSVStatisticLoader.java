/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

import java.nio.file.Path;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public abstract class CSVStatisticLoader {

    public abstract BoundedIndexContainer<CSVStatistic> createContainerFromCSV(Path csvPath)
            throws CSVReaderException;
}
