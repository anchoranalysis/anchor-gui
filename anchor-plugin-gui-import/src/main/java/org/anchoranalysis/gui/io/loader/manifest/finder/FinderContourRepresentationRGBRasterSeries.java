/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import org.anchoranalysis.gui.finder.FinderRasterFolder;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;

// currently unused, as we don't write the contour repersentation to the file system
class FinderContourRepresentationRGBRasterSeries extends FinderRasterFolder {

    public FinderContourRepresentationRGBRasterSeries(
            RasterReader rasterReader, String folderName) {
        super(folderName, "contourRepresentationRGB", rasterReader);
    }
}
