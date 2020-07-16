/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class FileCreator extends AnchorBean<FileCreator> {

    // START BEANS
    @BeanField private String customName = "";
    // END BEANS

    // Guaranteed to be called after addFilesToList
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    public abstract String suggestName();

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public abstract VideoStatsModule createModule(
            String name,
            FileCreatorParams params,
            VideoStatsModuleGlobalParams mpg,
            IAddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException;
}
