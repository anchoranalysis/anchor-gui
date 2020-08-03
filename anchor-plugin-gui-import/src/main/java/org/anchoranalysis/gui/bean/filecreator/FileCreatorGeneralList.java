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

package org.anchoranalysis.gui.bean.filecreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.SimpleInteractiveFileListInternalFrame;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class FileCreatorGeneralList extends FileCreator {

    public VideoStatsModule createModule(
            String name,
            FileCreatorParams params,
            VideoStatsModuleGlobalParams mpg,
            AddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {

        SimpleInteractiveFileListInternalFrame manifestListSummary =
                new SimpleInteractiveFileListInternalFrame(name);
        try {
            manifestListSummary.init(
                    adder,
                    pr -> files(pr, params),
                    fileOpenManager,
                    mpg,
                    params.getMarkCreatorParams().getMarkDisplaySettings(),
                    progressReporter);
        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
        return manifestListSummary.createVideoStatsModule(new DefaultModuleState());
    }

    protected abstract void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException;
    
    private List<InteractiveFile> files(ProgressReporter progressReporter, FileCreatorParams params) throws OperationFailedException {
        List<InteractiveFile> out = new ArrayList<>();
        addFilesToList(out, params, progressReporter);

        // Let's sort out list before we display them
        Collections.sort(out);
        return out;
    }
}
