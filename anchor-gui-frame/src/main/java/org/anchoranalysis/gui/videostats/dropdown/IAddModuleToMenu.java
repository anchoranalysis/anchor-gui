/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

public interface IAddModuleToMenu {

    VideoStatsModuleCreatorAndAdder addModuleToMenu(
            VideoStatsOperationMenu menu,
            ContextualModuleCreator creator,
            boolean useShortNames,
            VideoStatsModuleGlobalParams mpg)
            throws MenuAddException;
}
