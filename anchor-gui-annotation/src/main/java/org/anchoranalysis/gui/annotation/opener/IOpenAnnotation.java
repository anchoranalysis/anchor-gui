/* (C)2020 */
package org.anchoranalysis.gui.annotation.opener;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public interface IOpenAnnotation {

    public abstract InitAnnotation open(boolean useDefaultCfg, Logger logger)
            throws VideoStatsModuleCreateException;
}
