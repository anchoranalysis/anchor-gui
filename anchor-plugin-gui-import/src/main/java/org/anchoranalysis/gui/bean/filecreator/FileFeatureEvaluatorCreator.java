/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.input.FeatureListSrcBuilder;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.FeatureEvaluatorCreator;

public class FileFeatureEvaluatorCreator extends FileCreator {

    // START BEAN PROPERTIES
    @BeanField @NonEmpty @Getter @Setter
    private List<NamedBean<FeatureListProvider<FeatureInput>>> listFeatures = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter private NRGSchemeCreator nrgSchemeCreator;
    // END BEAN PROPERTIES

    @Override
    public String suggestName() {
        return "untitled feature-evaluator";
    }

    @Override
    public VideoStatsModule createModule(
            String name,
            FileCreatorParams params,
            VideoStatsModuleGlobalParams mpg,
            IAddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {

        try {
            FeatureEvaluatorCreator creator =
                    new FeatureEvaluatorCreator(createSrc(mpg.getContext()), mpg.getLogger());
            return creator.createVideoStatsModule(adder);

        } catch (CreateException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    private FeatureListSrc createSrc(CommonContext context) throws CreateException {
        return new FeatureListSrcBuilder(context.getLogger())
                .build(createInitParams(context), nrgSchemeCreator);
    }

    private SharedFeaturesInitParams createInitParams(CommonContext context)
            throws CreateException {
        SharedFeaturesInitParams soFeature =
                SharedFeaturesInitParams.create(new SharedObjects(context));
        try {
            soFeature.populate(listFeatures, context.getLogger());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
        return soFeature;
    }
}
