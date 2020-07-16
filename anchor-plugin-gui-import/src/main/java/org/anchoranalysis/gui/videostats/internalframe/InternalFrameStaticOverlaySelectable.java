/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;

public class InternalFrameStaticOverlaySelectable {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameStaticOverlaySelectable(String title, boolean sendReceiveIndices) {
        this.delegate =
                new InternalFrameOverlayedInstantStateToRGBSelectable(
                        title, false, sendReceiveIndices);
    }

    public ISliderState init(
            OverlayCollection oc, DefaultModuleState defaultState, VideoStatsModuleGlobalParams mpg)
            throws InitException {

        OverlayedInstantState cis = new OverlayedInstantState(0, oc);

        SingleContainer<OverlayedInstantState> cfgCntr = new SingleContainer<>(false);
        cfgCntr.setItem(cis, cis.getIndex());

        IDGetter<Overlay> idGetter = new IDGetterOverlayID();

        ISliderState sliderState =
                this.delegate.init(
                        cfgCntr,
                        mpg.getDefaultColorIndexForMarks(),
                        idGetter,
                        idGetter,
                        false,
                        defaultState,
                        mpg);

        this.delegate.setIndexSliderVisible(false);

        return sliderState;
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu(ISliderState sliderState) {
        return delegate.controllerBackgroundMenu(sliderState);
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
