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

package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.manifest.ManifestDropDown;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

// A file representing the results applied to an image within an executed experiment
public class FileExecutedExperimentImageWithManifest extends InteractiveFile {

    private ManifestDropDown manifestDropDown;

    private CoupledManifests coupledManifests;
    private RasterReader rasterReader;
    private MarkCreatorParams markCreatorParams;

    public FileExecutedExperimentImageWithManifest(
            CoupledManifests coupledManifests,
            RasterReader rasterReader,
            MarkCreatorParams markCreatorParams) {
        this.coupledManifests = coupledManifests;
        this.rasterReader = rasterReader;
        this.markCreatorParams = markCreatorParams;
    }

    @Override
    public OpenedFile open(final AddVideoStatsModule adder, final Outputter outputter)
            throws OperationFailedException {

        manifestDropDown =
                new ManifestDropDown(coupledManifests, markCreatorParams.getMarkDisplaySettings());

        try {
            manifestDropDown.init(
                    adder,
                    rasterReader,
                    markCreatorParams.getMarkEvaluatorManager(),
                    outputter,
                    markCreatorParams.getModuleParams());
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, manifestDropDown.openedFileGUI());
    }

    @Override
    public String identifier() {
        return coupledManifests.descriptiveName();
    }

    @Override
    public Optional<File> associatedFile() {
        return coupledManifests.pathForBinding().map(Path::toFile);
    }

    @Override
    public String type() {
        return "experiment results for image";
    }
}
