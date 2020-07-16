/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.frame.IGetToolbar;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class FileOpenManager implements IOpenFile {

    // The Parameters we need to perform the opening
    private IAddVideoStatsModule globalSubgroupAdder;
    private IGetToolbar videoStatsFrame;
    private BoundOutputManagerRouteErrors outputManager;

    // A map to keep track from currently opened-modules to the appropriate file counter
    private VideoStatsModuleMapToOpenedFileCounter mapModules;

    // A hash-map we use to keep track of all currently opened-files to the file counter
    private VideoStatsFileMapToOpenedFileCounter mapFile =
            new VideoStatsFileMapToOpenedFileCounter();

    public FileOpenManager(
            IAddVideoStatsModule globalSubgroupAdder,
            IGetToolbar videoStatsFrame,
            BoundOutputManagerRouteErrors outputManager) {
        super();
        this.globalSubgroupAdder = globalSubgroupAdder;
        this.videoStatsFrame = videoStatsFrame;
        this.outputManager = outputManager;
        this.mapModules = new VideoStatsModuleMapToOpenedFileCounter();
    }

    @Override
    public OpenedFile open(InteractiveFile file) throws OperationFailedException {

        OpenedFileCounter ofc = mapFile.get(file);

        if (ofc == null) {

            OpenedFileCounter counter =
                    new OpenedFileCounter(mapModules, mapFile, videoStatsFrame.getToolbar());

            AdderGUICountToolbar addToToolbarAdder =
                    new AdderGUICountToolbar(globalSubgroupAdder, counter);
            OpenedFile of = file.open(addToToolbarAdder, outputManager);
            counter.setOpenedFile(of);

            // We don't actually add this to our mapFile, until the first module is added, as
            // sometimes a user right-clicks
            //   and never actually creates a module, so it would be silly to add it to the cache

            return counter.getOpenedFile();
        }

        return ofc.getOpenedFile();
    }

    public void closeModule(VideoStatsModule module) {
        OpenedFileCounter counter = mapModules.get(module);

        if (counter != null) {
            if (counter.removeVideoStatsModule(module)) {
                // If it returns true, it means we have removed the last module associated with an
                // open file, and therefore
                //   we can remove it from our cache
                // System.out.println("Removing open file counter from mapFile");

                mapFile.remove(counter.getOpenedFile().getFile());
                mapModules.remove(module);

                // module.getComponent().dispose();

                System.gc();
            }
        } else {
            // NO FILE ASSOCIATED WITH MODULE, we're not sure if this is called in error for now,
            // let's write a message
            System.out.println("No file associated with module");
        }

        // To expedite garbage collection (maybe??)
        counter = null;
    }
}
