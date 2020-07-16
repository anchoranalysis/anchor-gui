/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.input;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.provider.FilePathProvider;
import org.anchoranalysis.io.input.InputFromManager;

public class InteractiveBrowserInput implements InputFromManager {

    @Getter @Setter private RasterReader rasterReader;

    @Getter @Setter private List<FileCreator> listFileCreators;

    @Setter private NRGSchemeCreator nrgSchemeCreator;

    @Setter private List<NamedBean<FeatureListProvider<FeatureInput>>> namedItemSharedFeatureList;

    @Getter @Setter private List<NamedBean<MarkEvaluator>> namedItemMarkEvaluatorList;

    @Setter private List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList;

    @Setter private List<NamedBean<FilePathProvider>> namedItemFilePathProviderList;

    @Getter @Setter private ImporterSettings importerSettings;

    public FeatureListSrc createFeatureListSrc(CommonContext context) throws CreateException {

        SharedObjects so = new SharedObjects(context);
        KeyValueParamsInitParams soParams = KeyValueParamsInitParams.create(so);
        SharedFeaturesInitParams soFeature = SharedFeaturesInitParams.create(so);

        try {
            // Adds the feature-lists to the shared-objects
            soFeature.populate(namedItemSharedFeatureList, context.getLogger());

            addKeyValueParams(soParams);
            addFilePaths(soParams);

            // so.g
        } catch (OperationFailedException e2) {
            throw new CreateException(e2);
        }

        return new FeatureListSrcBuilder(context.getLogger()).build(soFeature, nrgSchemeCreator);
    }

    private void addKeyValueParams(KeyValueParamsInitParams soParams)
            throws OperationFailedException {

        for (NamedBean<KeyValueParamsProvider> ni : this.namedItemKeyValueParamsProviderList) {
            soParams.getNamedKeyValueParamsCollection()
                    .add(ni.getName(), new OperationCreateFromProvider<>(ni.getValue()));
        }
    }

    private void addFilePaths(KeyValueParamsInitParams soParams) throws OperationFailedException {

        for (NamedBean<FilePathProvider> ni : this.namedItemFilePathProviderList) {
            soParams.getNamedFilePathCollection()
                    .add(ni.getName(), new OperationCreateFromProvider<>(ni.getValue()));
        }
    }

    @Override
    public String descriptiveName() {
        return "interactiveBrowserInput";
    }

    @Override
    public Optional<Path> pathForBinding() {
        return Optional.empty();
    }
}
