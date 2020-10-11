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

package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.ExecutedExperimentFileCreator;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.io.bean.files.provider.SpecificPathList;
import org.anchoranalysis.plugin.io.bean.input.manifest.CoupledManifestsInputManager;
import org.apache.commons.io.FilenameUtils;

public class Manifest extends OpenFileType {

    @Override
    public String[] getExtensions() {
        return new String[] {"ser"};
    }

    @Override
    public String getDescription() {
        return "Manifest";
    }

    @Override
    public List<FileCreator> creatorForFile(List<File> files, ImporterSettings importerSettings)
            throws CreateException {
        ExecutedExperimentFileCreator creator = new ExecutedExperimentFileCreator();

        creator.setCustomName(customNameForExperiment(files));

        List<String> fileListExp = new ArrayList<>();
        List<String> fileListInput = new ArrayList<>();

        populateFileList(files, fileListExp, fileListInput);

        creator.setCoupledManifestsInputManager(createCoupledManifests(fileListExp, fileListInput));

        return Arrays.asList(creator);
    }

    private static String customNameForExperiment(List<File> files) {
        if (files.size() == 1) {
            return String.format(
                    "experiment: %s", FilenameUtils.removeExtension(files.get(0).getName()));
        } else {
            return "experiment: multiple";
        }
    }

    private static void populateFileList(
            List<File> files, List<String> fileListExp, List<String> fileListInput) {
        for (File f : files) {
            if (f.getName().equals("manifestExperiment.ser")) {
                fileListExp.add(f.getPath());
            } else {
                fileListInput.add(f.getPath());
            }
        }
    }

    private static CoupledManifestsInputManager createCoupledManifests(
            List<String> fileListExp, List<String> fileListInput) {
        CoupledManifestsInputManager coupledManifests = new CoupledManifestsInputManager();

        coupledManifests.setManifestExperimentInputFileSet(new SpecificPathList(fileListExp));

        coupledManifests.setManifestInputFileSet(new SpecificPathList(fileListInput));

        return coupledManifests;
    }
}
