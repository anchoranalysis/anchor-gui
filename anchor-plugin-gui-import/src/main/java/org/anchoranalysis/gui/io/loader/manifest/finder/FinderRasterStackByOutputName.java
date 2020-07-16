/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;

public class FinderRasterStackByOutputName extends FinderRasterStack {

    private String outputName;

    public FinderRasterStackByOutputName(
            RasterReader rasterReader, String outputName, ErrorReporter errorReporter) {

        // TODO fix
        // Assumes its a trebble channel source
        super(rasterReader, errorReporter);
        this.outputName = outputName;
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {

        List<FileWrite> list =
                FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName(outputName));
        if (list.size() > 1) {
            throw new MultipleFilesException("cannot determine " + outputName + " exactly");
        }
        if (list.size() == 1) {
            return Optional.of(list.get(0));
        }

        return Optional.empty();
    }
}
