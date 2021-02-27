/*-
 * #%L
 * anchor-gui-browser
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

package org.anchoranalysis.gui.interactivebrowser.input;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.bean.shared.dictionary.DictionaryInitialization;
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.bean.shared.path.FilePathInitialization;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.bean.path.provider.FilePathProvider;
import org.anchoranalysis.mpp.feature.bean.energy.scheme.EnergySchemeCreator;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;

public class InteractiveBrowserInput implements InputFromManager {

    @Getter @Setter private StackReader stackReader;

    @Getter @Setter private List<FileCreator> listFileCreators;

    @Setter private EnergySchemeCreator energySchemeCreator;

    @Setter private List<NamedBean<FeatureListProvider<FeatureInput>>> featureLists;

    @Getter @Setter private List<NamedBean<MarkEvaluator>> markEvaluators;

    @Setter private List<NamedBean<DictionaryProvider>> dictionaries;

    @Setter private List<NamedBean<FilePathProvider>> filePaths;

    @Getter @Setter private ImporterSettings importerSettings;

    public FeatureListSrc createFeatureListSrc(CommonContext context) throws CreateException {

        SharedObjects sharedObjects = new SharedObjects(context);
        FeaturesInitialization initialization = FeaturesInitialization.create(sharedObjects);

        try {
            // Adds the feature-lists to the shared-objects
            initialization.populate(featureLists, context.getLogger());

            addDictionary(initialization.getDictionary());
            addFilePaths(initialization.getFilePaths());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return new FeatureListSrcBuilder(context.getLogger())
                .build(initialization, energySchemeCreator);
    }

    private void addDictionary(DictionaryInitialization initialization)
            throws OperationFailedException {

        for (NamedBean<DictionaryProvider> provider : this.dictionaries) {
            initialization
                    .getDictionaries()
                    .add(provider.getName(), cachedCreationFromProvider(provider.getValue()));
        }
    }

    private void addFilePaths(FilePathInitialization initialization)
            throws OperationFailedException {

        for (NamedBean<FilePathProvider> provider : this.filePaths) {
            initialization
                    .getFilePaths()
                    .add(provider.getName(), cachedCreationFromProvider(provider.getValue()));
        }
    }

    @Override
    public String name() {
        return "interactiveBrowserInput";
    }

    @Override
    public Optional<Path> pathForBinding() {
        return Optional.empty();
    }

    private static <T> StoreSupplier<T> cachedCreationFromProvider(Provider<T> provider) {
        return StoreSupplier.cache(
                () -> {
                    try {
                        return provider.create();
                    } catch (CreateException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }
}
