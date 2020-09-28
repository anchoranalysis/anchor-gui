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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class FileMultiCollection extends InteractiveFile {

    private MultiInput inputObject;
    private MarkCreatorParams markCreatorParams;

    public FileMultiCollection(MultiInput inputObject, MarkCreatorParams markCreatorParams) {
        super();
        this.inputObject = inputObject;
        this.markCreatorParams = markCreatorParams;
    }

    @Override
    public String identifier() {
        return inputObject.descriptiveName();
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
    public OpenedFile open(AddVideoStatsModule globalSubgroupAdder, Outputter outputter)
            throws OperationFailedException {

        LazyEvaluationStore<TimeSequence> stacks = new LazyEvaluationStore<>("stacks");
        inputObject.stack().addToStore(stacks);

        LazyEvaluationStore<MarkCollection> markss = new LazyEvaluationStore<>("marks");
        inputObject.marks().addToStore(markss);

        LazyEvaluationStore<KeyValueParams> keyValueParams =
                new LazyEvaluationStore<>("keyValueParams");
        inputObject.keyValueParams().addToStore(keyValueParams);

        LazyEvaluationStore<ObjectCollection> objects =
                new LazyEvaluationStore<>("object-collections");
        inputObject.objects().addToStore(objects);

        MultiCollectionDropDown dropDown =
                new MultiCollectionDropDown(
                        progressReporter -> createTimeSequenceProvider(stacks),
                        markss,
                        objects,
                        keyValueParams,
                        identifier(),
                        true);

        try {
            dropDown.init(globalSubgroupAdder, outputter, markCreatorParams);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }

    private TimeSequenceProvider createTimeSequenceProvider(
            LazyEvaluationStore<TimeSequence> stacks) throws CreateException {
        try {
            return new TimeSequenceProvider(stacks, inputObject.numberFrames());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
