/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameCfgNRGHistoryFolder;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class ColoredOutlineCreator extends VideoStatsModuleCreatorContext {

    private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
    private final OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            backgroundSet;

    public ColoredOutlineCreator(
            FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        super();
        this.finderCfgNRGHistory = finderCfgNRGHistory;
        this.backgroundSet = backgroundSet;
    }

    @Override
    public boolean precondition() {
        return finderCfgNRGHistory.exists();
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        InternalFrameCfgNRGHistoryFolder imageFrame =
                new InternalFrameCfgNRGHistoryFolder(namePrefix);
        try {
            ISliderState sliderState =
                    imageFrame.init(
                            finderCfgNRGHistory.get(),
                            defaultStateManager.getState(),
                            backgroundSet,
                            mpg);

            return Optional.of(imageFrame.moduleCreator(sliderState));

        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        } catch (GetOperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return "Colored Outline";
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.empty();
    }
}
