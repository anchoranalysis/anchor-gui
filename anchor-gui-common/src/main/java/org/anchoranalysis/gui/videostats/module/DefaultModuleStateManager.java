/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.videostats.module;

import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.link.DefaultLinkState;
import org.anchoranalysis.gui.videostats.link.DefaultLinkStateManager;
import org.anchoranalysis.image.stack.DisplayStack;

public class DefaultModuleStateManager {

    private DefaultLinkStateManager linkStateManager;
    private MarkDisplaySettings markDisplaySettings = new MarkDisplaySettings();

    public DefaultModuleStateManager(DefaultLinkState state) {
        this.linkStateManager = new DefaultLinkStateManager(state);
    }

    public DefaultLinkStateManager getLinkStateManager() {
        return linkStateManager;
    }

    // Changing the state here won't change any markDisplay Setitngs
    public DefaultModuleState getState() {
        return new DefaultModuleState(linkStateManager.getState(), markDisplaySettings);
    }

    public DefaultModuleState copy() {
        DefaultModuleState dms =
                new DefaultModuleState(linkStateManager.copy(), markDisplaySettings.duplicate());
        return dms;
    }

    /** Provides a copy of the default module state with a changed background */
    public DefaultModuleState copyChangeBackground(
            CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> background) {
        return new DefaultModuleState(
                linkStateManager.copyChangeBackground(background), markDisplaySettings);
    }

    public void setMarkDisplaySettings(MarkDisplaySettings markDisplaySettings) {
        this.markDisplaySettings = markDisplaySettings;
    }
}
