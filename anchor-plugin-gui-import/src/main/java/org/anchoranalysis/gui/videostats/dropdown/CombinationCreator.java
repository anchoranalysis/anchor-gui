/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgNRGSet;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.internalframe.MergedCfgNRGHistoryInternalFrame;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

class CombinationCreator extends VideoStatsModuleCreatorContext {

    private FinderCfgNRGSet finderFirst;
    private FinderCfgNRGSet finderSecond;
    private String combinationName;
    private OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet;

    public CombinationCreator(
            FinderCfgNRGSet finderFirst,
            FinderCfgNRGSet finderSecond,
            String combinationName,
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        super();
        this.finderFirst = finderFirst;
        this.finderSecond = finderSecond;
        this.combinationName = combinationName;
        this.backgroundSet = backgroundSet;
    }

    @Override
    public boolean precondition() {
        return true;
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        try {

            MergedCfgNRGHistoryInternalFrame imageFrame =
                    new MergedCfgNRGHistoryInternalFrame(combinationName);

            try {
                ISliderState sliderState =
                        imageFrame.init(
                                finderFirst.getHistory(),
                                finderSecond.getHistory(),
                                defaultStateManager.getState(),
                                mpg);

                imageFrame.controllerBackgroundMenu(sliderState).add(mpg, backgroundSet);

                return Optional.of(imageFrame.moduleCreator(sliderState));

            } catch (InitException e) {
                throw new VideoStatsModuleCreateException(e);
            }

        } catch (GetOperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return combinationName;
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.empty();
    }
}
