/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.filelist;

import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.SimpleVideoStatsFileListTableModel;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
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
            IAddVideoStatsModule adder,
            OperationWithProgressReporter<List<InteractiveFile>, OperationFailedException>
                    opListFile,
            IOpenFile fileOpenManager,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings,
            ProgressReporter progressReporter)
            throws InitException {

        try {
            delegate.init(
                    adder,
                    new SimpleVideoStatsFileListTableModel(opListFile, progressReporter),
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
