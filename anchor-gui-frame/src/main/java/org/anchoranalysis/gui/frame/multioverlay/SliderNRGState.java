/* (C)2020 */
package org.anchoranalysis.gui.frame.multioverlay;

import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;

class SliderNRGState {

    private ISliderState slider;
    private NRGBackground nrgBackground;

    public SliderNRGState(ISliderState slider, NRGBackground nrgBackground) {
        super();
        this.slider = slider;
        this.nrgBackground = nrgBackground;
    }

    public ISliderState getSlider() {
        return slider;
    }

    public IAddVideoStatsModule addNrgStackToAdder(IAddVideoStatsModule adder) {
        return nrgBackground.addNrgStackToAdder(adder);
    }
}
