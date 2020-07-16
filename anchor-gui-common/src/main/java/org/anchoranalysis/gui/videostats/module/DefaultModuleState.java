/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.link.DefaultLinkState;

public class DefaultModuleState {

    private DefaultLinkState linkState = new DefaultLinkState();
    private MarkDisplaySettings markDisplaySettings = new MarkDisplaySettings();

    public DefaultModuleState() {}

    public DefaultModuleState(DefaultLinkState linkState, MarkDisplaySettings markDisplaySettings) {
        super();
        this.linkState = linkState;
        this.markDisplaySettings = markDisplaySettings;
    }

    public MarkDisplaySettings getMarkDisplaySettings() {
        return markDisplaySettings;
    }

    public DefaultLinkState getLinkState() {
        return linkState;
    }
}
