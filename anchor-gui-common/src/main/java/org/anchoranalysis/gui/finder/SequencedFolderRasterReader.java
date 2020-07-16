/* (C)2020 */
package org.anchoranalysis.gui.finder;

import java.nio.file.Path;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.deserializer.folder.sequenced.SequencedFolderCntrCreator;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;

class SequencedFolderRasterReader extends SequencedFolderCntrCreator<Stack> {

    private RasterReader rasterReader;

    public SequencedFolderRasterReader(SequencedFolder rootFolder, RasterReader rasterReader) {
        super(rootFolder);
        this.rasterReader = rasterReader;
    }

    @Override
    protected Stack createFromFilePath(Path path) throws CreateException {
        // We don't support multiple series for now
        try {
            OpenedRaster or = rasterReader.openFile(path);
            try {
                Stack stack = or.open(0, ProgressReporterNull.get()).get(0);
                return stack.duplicate();
            } finally {
                or.close();
            }
        } catch (RasterIOException e) {
            throw new CreateException(e);
        }
    }
}
