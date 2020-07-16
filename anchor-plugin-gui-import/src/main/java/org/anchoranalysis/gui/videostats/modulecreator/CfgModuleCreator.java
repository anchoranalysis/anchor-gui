/* (C)2020 */
package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;

public class CfgModuleCreator extends VideoStatsModuleCreator {

    private String fileIdentifier;
    private String name;
    private Operation<Cfg, OperationFailedException> opCfg;
    private NRGBackground nrgBackground;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    public CfgModuleCreator(
            String fileIdentifier,
            String name,
            Operation<Cfg, OperationFailedException> opCfg,
            NRGBackground nrgBackground,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings) {
        super();
        this.fileIdentifier = fileIdentifier;
        this.name = name;
        this.opCfg = opCfg;
        this.nrgBackground = nrgBackground;
        this.mpg = mpg;
        this.markDisplaySettings = markDisplaySettings;
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            Cfg cfg = opCfg.doOperation();

            OverlayCollection oc =
                    OverlayCollectionMarkFactory.createWithoutColor(
                            cfg, markDisplaySettings.regionMembership());

            String frameName = String.format("%s: %s", fileIdentifier, name);
            InternalFrameStaticOverlaySelectable imageFrame =
                    new InternalFrameStaticOverlaySelectable(frameName, true);
            ISliderState sliderState =
                    imageFrame.init(
                            oc, adder.getSubgroup().getDefaultModuleState().getState(), mpg);

            imageFrame
                    .controllerBackgroundMenu(sliderState)
                    .addDefinition(
                            mpg,
                            new ChangeableBackgroundDefinitionSimple(
                                    nrgBackground.getBackgroundSet()));
            ModuleAddUtilities.add(adder, imageFrame.moduleCreator(sliderState));

        } catch (InitException | OperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.of(
                new IVideoStatsOperationCombine() {

                    @Override
                    public Optional<Operation<Cfg, OperationFailedException>> getCfg() {
                        return Optional.of(opCfg);
                    }

                    @Override
                    public String generateName() {
                        return fileIdentifier;
                    }

                    @Override
                    public Optional<Operation<ObjectCollection, OperationFailedException>>
                            getObjects() {
                        return Optional.empty();
                    }

                    @Override
                    public NRGBackground getNrgBackground() {
                        return nrgBackground;
                    }
                });
    }
}
