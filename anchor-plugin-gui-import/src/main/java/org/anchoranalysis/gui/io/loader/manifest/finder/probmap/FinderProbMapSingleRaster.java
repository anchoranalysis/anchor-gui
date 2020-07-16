/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.probmap;

import java.util.Optional;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderRasterChnlZeroOne;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.finder.MultipleFilesException;
import org.anchoranalysis.io.manifest.match.helper.filewrite.FileWriteFileFunctionTypeOutputName;

public class FinderProbMapSingleRaster extends FinderRasterChnlZeroOne {

    private String probMapOutputName;

    public FinderProbMapSingleRaster(
            RasterReader rasterReader, String probMapOutputName, ErrorReporter errorReporter) {
        super(rasterReader, errorReporter);
        this.probMapOutputName = probMapOutputName;
    }

    @Override
    protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
            throws MultipleFilesException {
        return FinderUtilities.findSingleItem(
                manifestRecorder,
                new FileWriteFileFunctionTypeOutputName("probmap", "raster", probMapOutputName));
    }
}
