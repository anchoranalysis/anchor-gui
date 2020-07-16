/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import java.util.HashMap;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;

class VideoStatsFileMapToOpenedFileCounter {

    private HashMap<InteractiveFile, OpenedFileCounter> delegate = new HashMap<>();

    public OpenedFileCounter get(Object arg0) {
        return delegate.get(arg0);
    }

    public OpenedFileCounter put(InteractiveFile arg0, OpenedFileCounter arg1) {
        return delegate.put(arg0, arg1);
    }

    public OpenedFileCounter remove(Object key) {
        return delegate.remove(key);
    }
}
