/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.probmap;

import org.anchoranalysis.gui.finder.FinderRasterFolder;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;

public class FinderProbMapRasterSeries extends FinderRasterFolder {

    public FinderProbMapRasterSeries(RasterReader rasterReader, String folderName) {
        super(folderName, "probMapSeries", rasterReader);
    }
}
