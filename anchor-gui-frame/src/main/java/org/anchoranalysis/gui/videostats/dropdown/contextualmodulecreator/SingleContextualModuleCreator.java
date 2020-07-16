/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class SingleContextualModuleCreator extends ContextualModuleCreator {

    private VideoStatsModuleCreatorContext moduleCreator;

    public SingleContextualModuleCreator(VideoStatsModuleCreatorContext moduleCreator) {
        super();
        this.moduleCreator = moduleCreator;
    }

    @Override
    public NamedModule[] create(
            String namePrefix,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            VideoStatsModuleGlobalParams mpg)
            throws CreateException {

        NamedModule namedModuleSingle = createSingle(namePrefix, adder, mpg);

        if (namedModuleSingle == null) {
            return new NamedModule[] {};
        }

        return new NamedModule[] {namedModuleSingle};
    }

    public NamedModule createSingle(
            String namePrefix,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            VideoStatsModuleGlobalParams mpg) {

        if (!moduleCreator.precondition()) {
            return null;
        }

        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(adder, moduleCreator.resolve(namePrefix, mpg));

        Optional<String> shortTitle = moduleCreator.shortTitle();
        if (shortTitle.isPresent()) {
            return new NamedModule(moduleCreator.title(), creatorAndAdder, shortTitle.get());
        } else {
            return new NamedModule(moduleCreator.title(), creatorAndAdder);
        }
    }
}
