/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.launch;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.filepath.provider.FilePathProvider;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;

public class InteractiveBrowserInputManager extends InputManager<InteractiveBrowserInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<FileCreator> listFileCreators = new ArrayList<>();

    @BeanField @DefaultInstance @Getter @Setter private RasterReader rasterReader;

    @BeanField @OptionalBean @Getter @Setter private NRGSchemeCreator nrgSchemeCreator;

    @BeanField @Getter @Setter
    private List<NamedBean<FeatureListProvider<FeatureInput>>> namedItemSharedFeatureList =
            new ArrayList<>();

    @BeanField @Getter @Setter
    private List<NamedBean<MarkEvaluator>> namedItemMarkEvaluatorList = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<KeyValueParamsProvider>> namedItemKeyValueParamsProviderList =
            new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathProvider>> namedItemFilePathProviderList = new ArrayList<>();

    @BeanField @Getter @Setter private ImporterSettings importerSettings;
    // END BEAN PROPERTIES

    @Override
    public List<InteractiveBrowserInput> inputObjects(InputManagerParams params)
            throws AnchorIOException {

        InteractiveBrowserInput ibi = new InteractiveBrowserInput();
        ibi.setListFileCreators(listFileCreators);
        ibi.setRasterReader(rasterReader);
        ibi.setNrgSchemeCreator(nrgSchemeCreator);
        ibi.setNamedItemSharedFeatureList(namedItemSharedFeatureList);
        ibi.setNamedItemMarkEvaluatorList(namedItemMarkEvaluatorList);
        ibi.setNamedItemKeyValueParamsProviderList(namedItemKeyValueParamsProviderList);
        ibi.setNamedItemFilePathProviderList(namedItemFilePathProviderList);
        ibi.setImporterSettings(importerSettings);

        return singletonList(ibi);
    }

    private static <T> List<T> singletonList(T elem) {
        List<T> singletonList = new ArrayList<>();
        singletonList.add(elem);
        return singletonList;
    }
}
