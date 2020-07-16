/* (C)2020 */
package org.anchoranalysis.gui.videostats.modulecreator;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.feature.evaluator.FeatureEvaluatorTableFrame;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class FeatureEvaluatorCreator extends VideoStatsModuleCreator {

    private FeatureListSrc featureListSrc;
    private Logger logger;

    public FeatureEvaluatorCreator(FeatureListSrc featureListSrc, Logger logger) {
        super();
        this.featureListSrc = featureListSrc;
        this.logger = logger;
    }

    public VideoStatsModule createVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            FeatureEvaluatorTableFrame mptf =
                    new FeatureEvaluatorTableFrame(
                            adder.getSubgroup().getDefaultModuleState().getState(),
                            featureListSrc,
                            true,
                            logger);
            return mptf.moduleCreator()
                    .createVideoStatsModule(adder.getSubgroup().getDefaultModuleState().getState());
        } catch (StatePanelUpdateException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        VideoStatsModule module = createVideoStatsModule(adder);
        adder.addVideoStatsModule(module);
    }
}
