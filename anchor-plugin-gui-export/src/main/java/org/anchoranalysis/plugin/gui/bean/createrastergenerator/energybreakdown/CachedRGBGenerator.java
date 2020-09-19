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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator.energybreakdown;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterWriterUtilities;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.TwoStageGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.writer.DrawOverlay;

@RequiredArgsConstructor
class CachedRGBGenerator extends CacheableOverlaysToRGBGenerator {

    // START BEAN PROPERTIES
    private final CachedRGB cachedRGB;

    /** Options that determine how the raster is written. */
    private final RasterWriteOptions rasterOptions;
    
    private final ErrorReporter errorReporter;
    // END BEAN PROPERTIES

    private OverlayedDisplayStackUpdate element;

    // THIS MUST BE CALLED before we do any drawing.
    @Override
    public void updateDrawer(DrawOverlay drawOverlay) {
        this.cachedRGB.updateDrawer(drawOverlay);
    }

    @Override
    public DisplayStack transform() throws OutputWriteFailedException {
        try {
            return DisplayStack.create(cachedRGB.getRGB());
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public OverlayedDisplayStackUpdate getIterableElement() {
        return element;
    }

    @Override
    public void applyRedrawUpdate(OverlayedDisplayStackUpdate update) {
        try {
            setIterableElement(update);
        } catch (SetOperationFailedException e) {
            errorReporter.recordError(CachedRGBGenerator.class, e);
        }
    }

    @Override
    public void setIterableElement(OverlayedDisplayStackUpdate element)
            throws SetOperationFailedException {

        // We don't do any changes if is exactly the same, if some other
        //   factor has altered, to change how the generator would
        //   produce it's output then, it must be externally triggered
        if (element == getIterableElement()) {
            return;
        }

        this.element = element;

        try {
            cachedRGB.updateMarks(element);
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
    }

    @Override
    public DisplayStack getBackground() {
        return cachedRGB.getBackground();
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        RasterGenerator.writeToFile(
                cachedRGB.getRGB().asStack(), outputWriteSettings, filePath, true);
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        return RasterWriterUtilities.fileExtensionForDefaultRasterWriter(outputWriteSettings, rasterOptions);
    }

    @Override
    public TwoStageGenerator<?, DisplayStack> getGenerator() {
        return this;
    }
}
