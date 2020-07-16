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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.IDGetterMarkID;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorFromDisplayStack;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.mpp.io.cfg.generator.SimpleOverlayWriter;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class ObjectWriterFromCfgNRGInstantState extends CreateRasterGenerator<CfgNRGInstantState> {

    // END BEAN PROPERTIES
    @BeanField @Getter @Setter private DrawObject drawObject;

    @BeanField @Getter @Setter private boolean mip = false;

    @BeanField @Getter @Setter private boolean colorFromIter = false;

    @BeanField @Getter @Setter private int borderSize = 3;

    @BeanField @Getter @Setter private String backgroundStackName = "input_image";
    // END BEAN PROPERTIES

    public ObjectWriterFromCfgNRGInstantState() {
        drawObject = new Outline(borderSize);
    }

    @Override
    public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
            final ExportTaskParams params) throws CreateException {

        final IterableObjectGenerator<OverlayedDisplayStackUpdate, Stack> generator;

        if (mip) {
            throw new CreateException("The mip flag is no longer supported for this bean");
        } else {

            // TODO the defaultImage for the cachedRGB should probably come from elsewhere
            CachedRGB cachedRGB = new CachedRGB(new IDGetterOverlayID());

            CfgCachedGenerator ccGenerator =
                    new CfgCachedGenerator(cachedRGB, params.getOutputManager().getErrorReporter());

            ccGenerator.updateMaskWriter(new SimpleOverlayWriter(drawObject));

            generator = new RasterGeneratorFromDisplayStack<>(ccGenerator, true);
        }

        return new IterableObjectGeneratorBridge<>(generator, elem -> bridgeElement(elem, params));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getFinderImgStackCollection() != null && params.getColorIndexMarks() != null;
    }

    @Override
    public String getBeanDscr() {
        return String.format(
                "%s(mip=%d, drawObject=%s)", getBeanName(), mip ? 1 : 0, drawObject.getBeanDscr());
    }

    private static Cfg extractOrEmpty(CfgNRG cfgNRG) {
        if (cfgNRG != null) {
            return cfgNRG.getCfg();
        } else {
            return new Cfg();
        }
    }

    private IDGetter<Mark> colorGetter() {
        if (colorFromIter) {
            return new IDGetterIter<>();
        } else {
            return new IDGetterMarkID();
        }
    }

    private OverlayedDisplayStackUpdate bridgeElement(
            MappedFrom<CfgNRGInstantState> sourceObject, ExportTaskParams params)
            throws OperationFailedException {
        try {
            Stack backgroundStackSrc =
                    params.getFinderImgStackCollection()
                            .getImgStackCollection()
                            .getException(backgroundStackName);

            DisplayStack backgroundStack = DisplayStack.create(backgroundStackSrc);

            ColoredCfg coloredCfg =
                    new ColoredCfg(
                            extractOrEmpty(sourceObject.getObj().getCfgNRG()),
                            params.getColorIndexMarks(),
                            colorGetter());

            RegionMembershipWithFlags regionMembership =
                    RegionMapSingleton.instance()
                            .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

            ColoredOverlayCollection oc =
                    OverlayCollectionMarkFactory.createColor(coloredCfg, regionMembership);
            return OverlayedDisplayStackUpdate.assignOverlaysAndBackground(oc, backgroundStack);

        } catch (CreateException | GetOperationFailedException e) {
            throw new OperationFailedException(e);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }
}
