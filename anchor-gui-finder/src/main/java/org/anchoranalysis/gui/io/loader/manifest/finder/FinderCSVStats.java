/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
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

    private Optional<BoundedIndexContainer<CSVStatistic>> statsCntr = Optional.empty();

    public FinderCSVStats(String outputName, ErrorReporter errorReporter) {
        super(errorReporter);
        this.outputName = outputName;
    }

    public BoundedIndexContainer<CSVStatistic> get() throws GetOperationFailedException {

        assert (exists());

        if (!statsCntr.isPresent()) {
            statsCntr = Optional.of(createContainer(getFoundFile()));
        }
        return statsCntr.get();
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {

        if (outputName.isEmpty()) {
            return Optional.empty();
        }

        List<FileWrite> found = findIncrementalCSVStats(manifestRecorder, "csv", outputName);

        if (found.size() >= 2) {
            throw new MultipleFilesException("Multiple matching manifest descriptions find");
        }

        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    private static List<FileWrite> findIncrementalCSVStats(
            ManifestRecorder manifestRecorder, String type, String outputName) {

        FileWriteAnd match = new FileWriteAnd();

        match.addCondition(
                new FileWriteManifestMatch(
                        new ManifestDescriptionMatchAnd(
                                new ManifestDescriptionMatchOr(
                                        new ManifestDescriptionFunctionMatch(
                                                "event_aggregate_stats"),
                                        new ManifestDescriptionFunctionMatch(
                                                "interval_aggregate_stats")),
                                new ManifestDescriptionTypeMatch(type))));

        match.addCondition(new FileWriteOutputName(outputName));

        ArrayList<FileWrite> foundList = new ArrayList<>();
        manifestRecorder.getRootFolder().findFile(foundList, match, false);
        return foundList;
    }

    private BoundedIndexContainer<CSVStatistic> createContainer(FileWrite fileWrite)
            throws GetOperationFailedException {

        try {
            CSVStatisticLoader loader =
                    createStatisticLoader(fileWrite.getManifestDescription().getFunction());
            return loader.createContainerFromCSV(fileWrite.calcPath());

        } catch (CSVReaderException e) {
            throw new GetOperationFailedException(e);
        }
    }

    private CSVStatisticLoader createStatisticLoader(String function)
            throws GetOperationFailedException {
        if (function.equals("event_aggregate_stats")) {
            return new CSVStatisticLoaderEventAggregate();
        } else if (function.equals("interval_aggregate_stats")) {
            return new CSVStatisticLoaderIntervalAggregate();
        } else {
            throw new GetOperationFailedException(
                    "Cannot determine which CSVStatisticLoader to use");
        }
    }
}
