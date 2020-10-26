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

package org.anchoranalysis.plugin.gui.bean.export.derivestack.energybreakdown;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.Redrawable;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.image.io.stack.output.generator.GeneratorOutputter;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.writer.DrawOverlay;

@RequiredArgsConstructor
class CachedRGBGenerator extends SingleFileTypeGenerator<OverlayedDisplayStackUpdate, DisplayStack>
        implements Redrawable {

    // START BEAN PROPERTIES
    private final CachedRGB cachedRGB;

    /** Options that determine how the raster is written. */
    private final StackWriteOptions rasterOptions;

    private final ErrorReporter errorReporter;
    // END BEAN PROPERTIES

    private OverlayedDisplayStackUpdate element;

    /** This <b>must be called</b> before any drawing. */
    public void updateDrawer(DrawOverlay drawOverlay) {
        this.cachedRGB.updateDrawer(drawOverlay);
    }

    @Override
    public DisplayStack transform(OverlayedDisplayStackUpdate element)
            throws OutputWriteFailedException {
        try {
            assignElement(element);
            return DisplayStack.create(cachedRGB.getRGB());
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public void applyRedrawUpdate(OverlayedDisplayStackUpdate update) {
        try {
            assignElement(update);
        } catch (OutputWriteFailedException e) {
            errorReporter.recordError(CachedRGBGenerator.class, e);
        }
    }

    public DisplayStack getBackground() {
        return cachedRGB.getBackground();
    }

    @Override
    public void writeToFile(
            OverlayedDisplayStackUpdate element,
            OutputWriteSettings outputWriteSettings,
            Path filePath)
            throws OutputWriteFailedException {
        try {
            assignElement(element);
            GeneratorOutputter.writer(outputWriteSettings)
                    .writeStack(cachedRGB.getRGB().asStack(), filePath, rasterOptions);
        } catch (ImageIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public String selectFileExtension(OutputWriteSettings outputWriteSettings)
            throws OperationFailedException {
        return GeneratorOutputter.fileExtensionWriter(outputWriteSettings, rasterOptions);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "marks"));
    }

    private void assignElement(OverlayedDisplayStackUpdate elementToAssign)
            throws OutputWriteFailedException {

        // We don't do any changes if is exactly the same, if some other
        //   factor has altered, to change how the generator would
        //   produce it's output then, it must be externally triggered
        if (elementToAssign != this.element) {
            this.element = elementToAssign;

            try {
                cachedRGB.updateMarks(elementToAssign);
            } catch (OperationFailedException e) {
                throw new OutputWriteFailedException(e);
            }
        }
    }
}
