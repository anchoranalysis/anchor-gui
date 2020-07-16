/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgWithNrgTotalInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgWithNrgTotalInstantStateBridge;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.ArrayListContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;

public class InternalFrameCfgNRGLive {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameCfgNRGLive(String title) {
        this.delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(title, true, true);
    }

    public ISliderState init(
            ArrayListContainer<CfgWithNrgTotalInstantState> ctnr,
            DefaultModuleState defaultState,
            BackgroundSet backgroundSet,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        IDGetter<Overlay> idGetter = new IDGetterOverlayID();

        ISliderState sliderState =
                this.delegate.init(
                        new BoundedIndexContainerBridgeWithoutIndex<>(
                                ctnr, new CfgWithNrgTotalInstantStateBridge()),
                        mpg.getDefaultColorIndexForMarks(),
                        idGetter,
                        idGetter,
                        true,
                        defaultState,
                        mpg);

        delegate.controllerBackgroundMenu(sliderState)
                .addDefinition(
                        mpg,
                        new ChangeableBackgroundDefinitionSimple(
                                new IdentityOperationWithProgressReporter<>(backgroundSet)));

        return sliderState;
    }

    public void flush() {
        delegate.flush();
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
