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

package org.anchoranalysis.gui.interactivebrowser.filelist;

import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.InteractiveFileSupplier;
import org.anchoranalysis.gui.interactivebrowser.SimpleVideoStatsFileListTableModel;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class SimpleInteractiveFileListInternalFrame {

    private InteractiveFileListInternalFrame delegate;

    public SimpleInteractiveFileListInternalFrame(String name) {
        delegate = new InteractiveFileListInternalFrame(name);
    }

    public void init(
            AddVideoStatsModule adder,
            InteractiveFileSupplier opListFile,
            IOpenFile fileOpenManager,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            Progress progress)
            throws InitException {

        try {
            delegate.init(
                    adder,
                    new SimpleVideoStatsFileListTableModel(opListFile, progress),
                    fileOpenManager,
                    mpg,
                    markDisplaySettings);
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
    }

    public VideoStatsModule createVideoStatsModule(DefaultModuleState defaultFrameState)
            throws VideoStatsModuleCreateException {
        return delegate.moduleCreator().createVideoStatsModule(defaultFrameState);
    }
}
