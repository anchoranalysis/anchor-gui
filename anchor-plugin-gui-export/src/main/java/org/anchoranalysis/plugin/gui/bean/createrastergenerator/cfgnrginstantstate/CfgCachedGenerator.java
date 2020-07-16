/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import java.nio.file.Path;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterWriterUtilities;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

class CfgCachedGenerator extends CacheableCfgToRGBGenerator {

    private CachedRGB cachedRGB;
    private OverlayedDisplayStackUpdate element;
    private ErrorReporter errorReporter;

    public CfgCachedGenerator(CachedRGB cachedRGB, ErrorReporter errorReporter) {
        super();
        this.cachedRGB = cachedRGB;
        this.errorReporter = errorReporter;
    }

    // THIS MUST BE CALLED before we do any drawing.
    @Override
    public void updateMaskWriter(DrawOverlay maskWriter) {
        this.cachedRGB.updateMaskWriter(maskWriter);
    }

    @Override
    public DisplayStack generate() throws OutputWriteFailedException {
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
            errorReporter.recordError(CfgCachedGenerator.class, e);
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
            cachedRGB.updateCfg(element);
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
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
    }
}
