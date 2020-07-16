/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.frame.IAddableToolbar;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

class OpenedFileCounter {

    private IAddableToolbar toolbar;
    private OpenedFile openedFile;
    private int moduleCnt = 0;
    private VideoStatsModuleMapToOpenedFileCounter mapOpenedFiles;
    private VideoStatsFileMapToOpenedFileCounter mapFile;

    public OpenedFileCounter(
            VideoStatsModuleMapToOpenedFileCounter mapOpenedFiles,
            VideoStatsFileMapToOpenedFileCounter mapFile,
            IAddableToolbar toolbar) {
        super();
        this.toolbar = toolbar;
        this.mapOpenedFiles = mapOpenedFiles;
        this.mapFile = mapFile;

        // System.out.println("Creating open file counter");
    }

    public void addVideoStatsModule(VideoStatsModule module) {
        if (moduleCnt == 0) {
            toolbar.add(openedFile.getGUI().getButton());

            // System.out.println("Saving mapFile to map");

            // As it's the first module we-add, we should also add the file to the map
            mapFile.put(openedFile.getFile(), this);
        }
        mapOpenedFiles.put(module, this);
        moduleCnt++;
    }

    public boolean removeVideoStatsModule(VideoStatsModule module) {
        OpenedFileCounter ofc = mapOpenedFiles.get(module);
        mapFile.remove(ofc.getOpenedFile().getFile());
        mapOpenedFiles.remove(module);
        moduleCnt--;

        if (moduleCnt == 0) {
            toolbar.removeRefresh(openedFile.getGUI().getButton());
            return true;
        }
        return false;
    }

    public void setOpenedFile(OpenedFile openedFile) {
        this.openedFile = openedFile;
    }

    public OpenedFile getOpenedFile() {
        return openedFile;
    }

    public int getModuleCnt() {
        return moduleCnt;
    }

    //	@Override
    //	protected void finalize() throws Throwable {
    //		super.finalize();
    //		System.out.println("Removing open file counter");
    //	}
}
