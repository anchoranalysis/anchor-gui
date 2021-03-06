/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.bean.filecreator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.input.FeatureListSrcBuilder;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.FeatureEvaluatorCreator;
import org.anchoranalysis.mpp.feature.bean.energy.scheme.EnergySchemeCreator;

public class FileFeatureEvaluatorCreator extends FileCreator {

    // START BEAN PROPERTIES
    @BeanField @NonEmpty @Getter @Setter
    private List<NamedBean<FeatureListProvider<FeatureInput>>> listFeatures = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter private EnergySchemeCreator energySchemeCreator;
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
            AddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            Progress progress)
            throws VideoStatsModuleCreateException {

        try {
            FeatureEvaluatorCreator creator =
                    new FeatureEvaluatorCreator(createSource(mpg.getContext()), mpg.getLogger());
            return creator.createVideoStatsModule(adder);

        } catch (CreateException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    private FeatureListSrc createSource(CommonContext context) throws CreateException {
        return new FeatureListSrcBuilder(context.getLogger())
                .build(createInitialization(context), energySchemeCreator);
    }

    private FeaturesInitialization createInitialization(CommonContext context)
            throws CreateException {
        FeaturesInitialization soFeature =
                FeaturesInitialization.create(new SharedObjects(context));
        try {
            soFeature.populate(listFeatures, context.getLogger());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
        return soFeature;
    }
}
