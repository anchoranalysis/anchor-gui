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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.OverlayCollectionSupplier;
import org.anchoranalysis.gui.videostats.operation.combine.VideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;

/**
 * @author Owen Feehan
 * @param <T> overlay-collection type
 */
@AllArgsConstructor
public abstract class OverlayedCollectionModuleCreator<T> extends VideoStatsModuleCreator {

    private String fileIdentifier;
    private String name;
    private OverlayCollectionSupplier<T> supplier;
    private EnergyBackground energyBackground;
    private VideoStatsModuleGlobalParams mpg;

    @Override
    public void createAndAddVideoStatsModule(AddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {
        try {
            addFrame(createFrame(frameName()), createOverlays(supplier.get()), adder);

        } catch (OperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public Optional<VideoStatsOperationCombine> getCombiner() {
        return Optional.of(
                new VideoStatsOperationCombine() {

                    @Override
                    public Optional<OverlayCollectionSupplier<MarkCollection>> getMarks() {
                        return marksSupplier();
                    }

                    @Override
                    public String generateName() {
                        return fileIdentifier;
                    }

                    @Override
                    public Optional<OverlayCollectionSupplier<ObjectCollection>> getObjects() {
                        return objectsSupplier();
                    }

                    @Override
                    public EnergyBackground getEnergyBackground() {
                        return energyBackground;
                    }
                });
    }

    protected abstract OverlayCollection createOverlays(T initialContents);

    protected abstract InternalFrameStaticOverlaySelectable createFrame(String frameName);

    protected abstract Optional<OverlayCollectionSupplier<MarkCollection>> marksSupplier();

    protected abstract Optional<OverlayCollectionSupplier<ObjectCollection>> objectsSupplier();

    protected OverlayCollectionSupplier<T> supplier() {
        return supplier;
    }

    private void addFrame(
            InternalFrameStaticOverlaySelectable imageFrame,
            OverlayCollection overlays,
            AddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {
        try {
            SliderState sliderState =
                    imageFrame.init(
                            overlays, adder.getSubgroup().getDefaultModuleState().getState(), mpg);

            imageFrame
                    .controllerBackgroundMenu(sliderState)
                    .add(mpg, energyBackground.getBackgroundSet());
            ModuleAddUtilities.add(adder, imageFrame.moduleCreator(sliderState));
        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    private String frameName() {
        return String.format("%s: %s", fileIdentifier, name);
    }
}
