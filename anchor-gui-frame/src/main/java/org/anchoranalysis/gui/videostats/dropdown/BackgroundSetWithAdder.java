/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.gui.backgroundset.BackgroundSet;

public class BackgroundSetWithAdder {
    private IAddVideoStatsModule adder;
    private BackgroundSet backgroundSet;

    public IAddVideoStatsModule getAdder() {
        return adder;
    }

    public void setAdder(IAddVideoStatsModule adder) {
        this.adder = adder;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }
}
