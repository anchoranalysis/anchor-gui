/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.SimpleInteractiveFileListInternalFrame;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class FileCreatorGeneralList extends FileCreator {

    public abstract void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException;

    public VideoStatsModule createModule(
            String name,
            final FileCreatorParams params,
            VideoStatsModuleGlobalParams mpg,
            IAddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            final ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {

        // Operation to retrieve files
        OperationWithProgressReporter<List<InteractiveFile>, OperationFailedException> op =
                pr -> {
                    List<InteractiveFile> listFiles = new ArrayList<>();
                    addFilesToList(listFiles, params, pr);

                    // Let's sort out list before we display them
                    Collections.sort(listFiles);
                    return listFiles;
                };

        SimpleInteractiveFileListInternalFrame manifestListSummary =
                new SimpleInteractiveFileListInternalFrame(name);
        try {
            manifestListSummary.init(
                    adder,
                    op,
                    fileOpenManager,
                    mpg,
                    params.getMarkCreatorParams().getMarkDisplaySettings(),
                    progressReporter);
        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
        return manifestListSummary.createVideoStatsModule(new DefaultModuleState());
    }
}
