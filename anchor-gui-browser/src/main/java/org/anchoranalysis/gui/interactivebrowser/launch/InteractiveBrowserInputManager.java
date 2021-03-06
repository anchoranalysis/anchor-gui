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

package org.anchoranalysis.gui.interactivebrowser.launch;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.stack.reader.InputManagerWithStackReader;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.InputsWithDirectory;
import org.anchoranalysis.io.input.bean.InputManagerParams;
import org.anchoranalysis.io.input.bean.path.provider.FilePathProvider;
import org.anchoranalysis.mpp.feature.bean.energy.scheme.EnergySchemeCreator;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;

public class InteractiveBrowserInputManager
        extends InputManagerWithStackReader<InteractiveBrowserInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<FileCreator> listFileCreators = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter private EnergySchemeCreator energySchemeCreator;

    @BeanField @Getter @Setter
    private List<NamedBean<FeatureListProvider<FeatureInput>>> featuresShared = new ArrayList<>();

    @BeanField @Getter @Setter
    private List<NamedBean<MarkEvaluator>> markEvaluators = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<DictionaryProvider>> dictionaries = new ArrayList<>();

    @BeanField @OptionalBean @Getter @Setter
    private List<NamedBean<FilePathProvider>> filePaths = new ArrayList<>();

    @BeanField @Getter @Setter private ImporterSettings importerSettings;
    // END BEAN PROPERTIES

    @Override
    public InputsWithDirectory<InteractiveBrowserInput> inputs(InputManagerParams params)
            throws InputReadFailedException {

        InteractiveBrowserInput input = new InteractiveBrowserInput();
        input.setListFileCreators(listFileCreators);
        input.setStackReader(getStackReader());
        input.setEnergySchemeCreator(energySchemeCreator);
        input.setFeatureLists(featuresShared);
        input.setMarkEvaluators(markEvaluators);
        input.setDictionaries(dictionaries);
        input.setFilePaths(filePaths);
        input.setImporterSettings(importerSettings);

        return new InputsWithDirectory<>(singletonList(input));
    }

    private static <T> List<T> singletonList(T elemelement) {
        List<T> list = new ArrayList<>();
        list.add(elemelement);
        return list;
    }
}
