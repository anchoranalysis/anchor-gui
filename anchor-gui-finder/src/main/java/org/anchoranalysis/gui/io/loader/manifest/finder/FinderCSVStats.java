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

package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
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

    private final String outputName;

    private Optional<BoundedIndexContainer<CSVStatistic>> statsCntr = Optional.empty();

    public FinderCSVStats(String outputName, ErrorReporter errorReporter) {
        super(errorReporter);
        this.outputName = outputName;
    }

    public BoundedIndexContainer<CSVStatistic> get() throws OperationFailedException {

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
            throws OperationFailedException {

        try {
            CSVStatisticLoader loader =
                    createStatisticLoader(fileWrite.getManifestDescription().getFunction());
            return loader.createContainerFromCSV(fileWrite.calcPath());

        } catch (CSVReaderException e) {
            throw new OperationFailedException(e);
        }
    }

    private CSVStatisticLoader createStatisticLoader(String function)
            throws OperationFailedException {
        if (function.equals("event_aggregate_stats")) {
            return new CSVStatisticLoaderEventAggregate();
        } else if (function.equals("interval_aggregate_stats")) {
            return new CSVStatisticLoaderIntervalAggregate();
        } else {
            throw new OperationFailedException("Cannot determine which CSVStatisticLoader to use");
        }
    }
}
