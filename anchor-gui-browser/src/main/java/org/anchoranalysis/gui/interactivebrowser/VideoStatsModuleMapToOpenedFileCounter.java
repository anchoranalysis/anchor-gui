/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import java.util.HashMap;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

class VideoStatsModuleMapToOpenedFileCounter {

    private HashMap<VideoStatsModule, OpenedFileCounter> delegate = new HashMap<>();

    public OpenedFileCounter get(Object arg0) {
        return delegate.get(arg0);
    }

    public OpenedFileCounter put(VideoStatsModule arg0, OpenedFileCounter arg1) {
        return delegate.put(arg0, arg1);
    }

    public OpenedFileCounter remove(Object key) {
        return delegate.remove(key);
    }
}
