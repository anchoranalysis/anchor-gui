/* (C)2020 */
package org.anchoranalysis.gui.annotation.additional;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;

public class ShowAdditionalRasters {

    private ShowRaster showRaster;
    private List<FilePathGenerator> listFilePathGenerator;
    private Path matchPath;
    private String name;
    private RasterReader rasterReader;

    public ShowAdditionalRasters(
            ShowRaster showRaster,
            List<FilePathGenerator> listFilePathGenerator,
            Path matchPath,
            String name,
            RasterReader rasterReader) {
        super();
        this.showRaster = showRaster;
        this.listFilePathGenerator = listFilePathGenerator;
        this.matchPath = matchPath;
        this.name = name;
        this.rasterReader = rasterReader;
    }

    public void apply() throws OperationFailedException {

        try {
            // Any additional image windows to be opened
            for (FilePathGenerator filePathGenerator : listFilePathGenerator) {
                Path rasterPath = filePathGenerator.outFilePath(matchPath, false);
                showRaster.openAndShow(name, rasterPath, rasterReader);
            }
        } catch (AnchorIOException | InitException | GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
