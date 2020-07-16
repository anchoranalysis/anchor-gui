/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class VideoStatsModuleCreateException extends AnchorCheckedException {

    private static final long serialVersionUID = 2351541381245189315L;

    public VideoStatsModuleCreateException(String string) {
        super(string);
    }

    public VideoStatsModuleCreateException(Throwable exc) {
        super(exc);
    }
}
