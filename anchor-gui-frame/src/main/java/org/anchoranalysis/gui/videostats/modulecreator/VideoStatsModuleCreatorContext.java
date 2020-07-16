/* (C)2020 */
package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class VideoStatsModuleCreatorContext {

    public abstract boolean precondition();

    public abstract Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException;

    /** The title of the module. Must be defined. */
    public abstract String title();

    /** The short-title of the module if it exists */
    public abstract Optional<String> shortTitle();

    public VideoStatsModuleCreator resolve(String namePrefix, VideoStatsModuleGlobalParams mpg) {
        return new VideoStatsModuleCreator() {

            @Override
            public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
                    throws VideoStatsModuleCreateException {

                DefaultModuleStateManager defaultStateManager =
                        adder.getSubgroup().getDefaultModuleState();

                OptionalUtilities.ifPresent(
                        moduleCreator(defaultStateManager, namePrefix, mpg),
                        creator -> ModuleAddUtilities.add(adder, creator));
            }
        };
    }
}
