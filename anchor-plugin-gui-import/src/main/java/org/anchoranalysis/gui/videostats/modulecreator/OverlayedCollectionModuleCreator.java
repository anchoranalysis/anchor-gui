package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
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
