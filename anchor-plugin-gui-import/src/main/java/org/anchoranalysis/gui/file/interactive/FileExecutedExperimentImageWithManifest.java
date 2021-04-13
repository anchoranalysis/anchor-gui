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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUIWithFile;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.manifest.ManifestDropDown;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

/**
 * A file representing the results applied to an image within an executed experiment
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class FileExecutedExperimentImageWithManifest extends InteractiveFile {

    // START REQUIRED ARGUMENTS
    private final CoupledManifests coupledManifests;
    private final StackReader stackReader;
    private final MarkCreatorParams markCreatorParams;
    // END REQUIRED ARGUMENTS

    private ManifestDropDown manifestDropDown;

    @Override
    public OpenedFile open(final AddVideoStatsModule adder, final InputOutputContext context)
            throws OperationFailedException {

        manifestDropDown =
                new ManifestDropDown(coupledManifests, markCreatorParams.getMarkDisplaySettings());

        try {
            manifestDropDown.init(
                    adder,
                    stackReader,
                    markCreatorParams.getMarkEvaluatorManager(),
                    context,
                    markCreatorParams.getModuleParams());
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUIWithFile(this, manifestDropDown.openedFileGUI());
    }

    @Override
    public String identifier() {
        return coupledManifests.identifier();
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
