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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.MarksWithEnergyBreakdown;
import org.anchoranalysis.anchor.mpp.mark.ColoredMarks;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.IDGetterMarkID;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorFromDisplayStack;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.mpp.io.marks.generator.SimpleOverlayWriter;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.GeneratorFactory;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class DrawObjects extends GeneratorFactory<IndexableMarksWithEnergy> {

    // END BEAN PROPERTIES
    @BeanField @Getter @Setter private DrawObject drawObject;

    @BeanField @Getter @Setter private boolean mip = false;

    @BeanField @Getter @Setter private boolean colorFromIter = false;

    @BeanField @Getter @Setter private int borderSize = 3;

    @BeanField @Getter @Setter private String backgroundStackName = "input_image";
    // END BEAN PROPERTIES

    public DrawObjects() {
        drawObject = new Outline(borderSize);
    }

    @Override
    public IterableObjectGenerator<MappedFrom<IndexableMarksWithEnergy>, Stack> createGenerator(
            final ExportTaskParams params) throws CreateException {

        final IterableObjectGenerator<OverlayedDisplayStackUpdate, Stack> generator;

        if (mip) {
            throw new CreateException("The mip flag is no longer supported for this bean");
        } else {

            // TODO the defaultImage for the cachedRGB should probably come from elsewhere
            CachedRGB cachedRGB = new CachedRGB(new IDGetterOverlayID());

            CachedRGBGenerator ccGenerator =
                    new CachedRGBGenerator(cachedRGB, params.getOutputManager().getErrorReporter());

            ccGenerator.updateDrawer(new SimpleOverlayWriter(drawObject));

            generator = new RasterGeneratorFromDisplayStack<>(ccGenerator, true);
        }

        return new IterableObjectGeneratorBridge<>(generator, elem -> bridgeElement(elem, params));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getFinderStacks() != null && params.getColorIndexMarks() != null;
    }

    @Override
    public String descriptionBean() {
        return String.format(
                "%s(mip=%d, drawObject=%s)", getBeanName(), mip ? 1 : 0, drawObject.descriptionBean());
    }

    private static MarkCollection extractOrEmpty(MarksWithEnergyBreakdown marks) {
        if (marks != null) {
            return marks.getMarks();
        } else {
            return new MarkCollection();
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
            MappedFrom<IndexableMarksWithEnergy> sourceObject, ExportTaskParams params)
            throws OperationFailedException {
        try {
            Stack backgroundStackSrc =
                    params.getFinderStacks().getStacks().getException(backgroundStackName);

            DisplayStack backgroundStack = DisplayStack.create(backgroundStackSrc);

            ColoredMarks coloredMarks =
                    new ColoredMarks(
                            extractOrEmpty(sourceObject.getObject().getMarks()),
                            params.getColorIndexMarks(),
                            colorGetter());

            RegionMembershipWithFlags regionMembership =
                    RegionMapSingleton.instance()
                            .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

            ColoredOverlayCollection oc =
                    OverlayCollectionMarkFactory.createColor(coloredMarks, regionMembership);
            return OverlayedDisplayStackUpdate.assignOverlaysAndBackground(oc, backgroundStack);

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }
}
