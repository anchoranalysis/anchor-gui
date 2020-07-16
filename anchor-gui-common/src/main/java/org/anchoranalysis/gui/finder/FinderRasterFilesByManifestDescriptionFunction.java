/* (C)2020 */
package org.anchoranalysis.gui.finder;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderUtilities;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionMatchAnd;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionTypeMatch;

public class FinderRasterFilesByManifestDescriptionFunction implements Finder {

    private String function;

    private List<FileWrite> list;

    private RasterReader rasterReader;

    public FinderRasterFilesByManifestDescriptionFunction(
            RasterReader rasterReader, String function) {

        this.function = function;
        this.rasterReader = rasterReader;
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {

        ManifestDescriptionMatchAnd matchManifest = new ManifestDescriptionMatchAnd();
        matchManifest.addCondition(new ManifestDescriptionFunctionMatch(function));
        matchManifest.addCondition(new ManifestDescriptionTypeMatch("raster"));

        list =
                FinderUtilities.findListFile(
                        manifestRecorder, new FileWriteManifestMatch(matchManifest));
        return exists();
    }

    @Override
    public boolean exists() {
        return list != null && !list.isEmpty();
    }

    public NamedImgStackCollection createStackCollection() {

        NamedImgStackCollection out = new NamedImgStackCollection();
        for (FileWrite fileWrite : list) {
            String name = fileWrite.getIndex();

            // Assume single series, single channel
            Path filePath = fileWrite.calcPath();

            out.addImageStack(name, new CachedOpenStackOp(filePath, rasterReader));
        }
        return out;
    }

    private static class CachedOpenStackOp
            extends CachedOperationWithProgressReporter<Stack, OperationFailedException> {

        private Path filePath;
        private RasterReader rasterReader;

        public CachedOpenStackOp(Path filePath, RasterReader rasterReader) {
            super();
            this.filePath = filePath;
            this.rasterReader = rasterReader;
        }

        @Override
        protected Stack execute(ProgressReporter progressReporter) throws OperationFailedException {
            try (OpenedRaster openedRaster = rasterReader.openFile(filePath)) {
                return openedRaster.open(0, progressReporter).get(0);

            } catch (RasterIOException e) {
                throw new OperationFailedException(e);
            }
        }
    }
}
