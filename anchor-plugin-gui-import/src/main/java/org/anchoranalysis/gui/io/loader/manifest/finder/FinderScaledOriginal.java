/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.FileWriteOutputName;

public class FinderScaledOriginal extends FinderRasterStack {

    public FinderScaledOriginal(RasterReader rasterReader, ErrorReporter errorReporter) {
        // TODO fix
        // Assumes its a single channel source
        super(rasterReader, errorReporter);
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {

        List<FileWrite> scaledOriginalList =
                FinderUtilities.findListFile(
                        manifestRecorder,
                        new FileWriteOutputName("stack_" + ImgStackIdentifiers.INPUT_IMAGE));
        if (scaledOriginalList.size() > 1) {
            throw new MultipleFilesException("cannot determine scaledOriginal exactly");
        }
        if (scaledOriginalList.size() == 1) {
            return Optional.of(scaledOriginalList.get(0));
        }

        // We look for an original instead, as maybe scaling isn't used
        List<FileWrite> originalList =
                FinderUtilities.findListFile(manifestRecorder, new FileWriteOutputName("original"));

        if (originalList.size() > 1) {
            throw new MultipleFilesException("cannot determine original exactly");
        }
        if (originalList.size() == 1) {
            return Optional.of(originalList.get(0));
        }

        return Optional.empty();
    }
}
