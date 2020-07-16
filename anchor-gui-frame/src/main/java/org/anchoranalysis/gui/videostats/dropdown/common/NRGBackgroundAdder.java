/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;

public class NRGBackgroundAdder<E extends Exception> {

    private NRGBackground delegate;
    private OperationWithProgressReporter<IAddVideoStatsModule, E> adder;

    public NRGBackgroundAdder(
            NRGBackground delegate, OperationWithProgressReporter<IAddVideoStatsModule, E> adder) {
        super();
        this.delegate = delegate;
        this.adder = adder;
    }

    public NRGBackground getNRGBackground() {
        return delegate;
    }

    public OperationWithProgressReporter<IAddVideoStatsModule, E> getAdder() {
        return adder;
    }
}
