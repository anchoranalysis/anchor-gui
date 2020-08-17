/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.image.object.ObjectCollection;

public class CfgModuleCreator extends OverlayedCollectionModuleCreator<Cfg> {

    private MarkDisplaySettings markDisplaySettings;

    public CfgModuleCreator(
            String fileIdentifier,
            String name,
            OverlayCollectionSupplier<Cfg> supplier,
            NRGBackground nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings) {
        super(fileIdentifier, name, supplier, nrgBackground, mpg);
        this.markDisplaySettings = markDisplaySettings;
    }

    @Override
    protected OverlayCollection createOverlays(Cfg initialContents) {
        return OverlayCollectionMarkFactory.createWithoutColor(
                initialContents, markDisplaySettings.regionMembership());
    }

    @Override
    protected InternalFrameStaticOverlaySelectable createFrame(String frameName) {
        return new InternalFrameStaticOverlaySelectable(frameName, true);
    }

    @Override
    protected Optional<OverlayCollectionSupplier<Cfg>> cfgSupplier() {
        return Optional.of(supplier());
    }

    @Override
    protected Optional<OverlayCollectionSupplier<ObjectCollection>> objectsSupplier() {
        return Optional.empty();
    }
}
