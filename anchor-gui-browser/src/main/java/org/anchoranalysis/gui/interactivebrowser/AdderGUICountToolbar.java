/*-
 * #%L
 * anchor-gui-browser
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

package org.anchoranalysis.gui.interactivebrowser;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public class AdderGUICountToolbar implements AddVideoStatsModule {

    private AddVideoStatsModule delegate;
    private OpenedFileCounter openedFileCounter;

    public AdderGUICountToolbar(AddVideoStatsModule delegate, OpenedFileCounter openedFileCounter) {
        super();
        this.delegate = delegate;
        this.openedFileCounter = openedFileCounter;
    }

    @Override
    public void addVideoStatsModule(VideoStatsModule module) {
        openedFileCounter.addVideoStatsModule(module);
        delegate.addVideoStatsModule(module);
    }

    @Override
    public VideoStatsModuleSubgroup getSubgroup() {
        return delegate.getSubgroup();
    }

    @Override
    public JFrame getParentFrame() {
        return delegate.getParentFrame();
    }

    @Override
    public AddVideoStatsModule createChild() {
        return new AdderGUICountToolbar(delegate.createChild(), openedFileCounter);
    }
}
