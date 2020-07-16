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
import org.anchoranalysis.core.name.store.EagerEvaluationStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.ExtractTimeSequenceFromInput;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.plugin.io.bean.input.stack.StackSequenceInput;
import org.apache.commons.io.FilenameUtils;

public class FileStackCollection extends InteractiveFile {

    private StackSequenceInput inputObject;
    private MarkCreatorParams params;

    public FileStackCollection(StackSequenceInput inputObject, MarkCreatorParams params) {
        super();
        this.inputObject = inputObject;
        this.params = params;
    }

    @Override
    public String identifier() {
        return FilenameUtils.removeExtension(new File(inputObject.descriptiveName()).getName());
    }

    @Override
    public Optional<File> associatedFile() {
        return inputObject.pathForBinding().map(Path::toFile);
    }

    @Override
    public String type() {
        return "raster";
    }

    @Override
    public OpenedFile open(
            IAddVideoStatsModule globalSubgroupAdder, BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException {

        MultiCollectionDropDown dropDown =
                new MultiCollectionDropDown(
                        new ExtractTimeSequenceFromInput(inputObject),
                        null,
                        null,
                        new EagerEvaluationStore<KeyValueParams>(),
                        identifier(),
                        false);

        try {
            dropDown.init(globalSubgroupAdder, outputManager, params);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }
}
