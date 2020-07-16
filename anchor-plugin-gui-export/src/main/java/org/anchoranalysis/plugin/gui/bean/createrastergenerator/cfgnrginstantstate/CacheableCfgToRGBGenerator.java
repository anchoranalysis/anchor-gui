/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import java.util.Optional;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class CacheableCfgToRGBGenerator extends ObjectGenerator<DisplayStack>
        implements IRedrawable, IterableObjectGenerator<OverlayedDisplayStackUpdate, DisplayStack> {

    // THIS MUST BE CALLED before we do any drawing.
    public abstract void updateMaskWriter(DrawOverlay maskWriter);

    // public abstract ColoredCfg getColoredCfg();

    public abstract DisplayStack getBackground();

    @Override
    public ObjectGenerator<DisplayStack> getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "cfg"));
    }

    @Override
    public void start() throws OutputWriteFailedException {}

    @Override
    public void end() throws OutputWriteFailedException {}
}
