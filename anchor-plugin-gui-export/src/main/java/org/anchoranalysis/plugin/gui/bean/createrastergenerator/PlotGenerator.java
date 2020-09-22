/*-
 * #%L
 * anchor-plugin-gui-export
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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.spatial.SizeXY;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorWithElement;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.bufferedimage.CreateStackFromBufferedImage;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plot.io.PlotOutputter;
import org.jfree.chart.ChartUtils;
import lombok.AllArgsConstructor;

/**
 * Writes a plot to the filesystem as a PNG file.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class PlotGenerator extends RasterGeneratorWithElement<PlotInstance> {

    private static final String MANIFEST_FUNCTION = "plot";

    /** Width/height of raster-image into which the plot is rendered. */
    private final SizeXY size;
    
    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        try (FileOutputStream fileOutput = new FileOutputStream(filePath.toFile())) {
            ChartUtils.writeChartAsPNG(fileOutput, getElement().getChart(), size.getWidth(), size.getHeight());
        } catch (IOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return "png";
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", MANIFEST_FUNCTION));
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {

        BufferedImage bufferedImage = PlotOutputter.createBufferedImage(getElement(), size.getWidth(), size.getHeight());

        try {
            return CreateStackFromBufferedImage.create(bufferedImage);
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public RasterWriteOptions rasterWriteOptions() {
        return RasterWriteOptions.rgbAlways2D();
    }
}
