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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.LazyEvaluationStore;
import org.anchoranalysis.core.value.KeyValueParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUIWithFile;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.io.input.MultiInput;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;

@AllArgsConstructor
public class FileMultiCollection extends InteractiveFile {

    private MultiInput input;
    private MarkCreatorParams markCreatorParams;

    @Override
    public String identifier() {
        return input.name();
    }

    @Override
    public Optional<File> associatedFile() {
        return input.pathForBinding().map(Path::toFile);
    }

    @Override
    public String type() {
        return "raster";
    }

    @Override
    public OpenedFile open(AddVideoStatsModule globalSubgroupAdder, InputOutputContext context)
            throws OperationFailedException {

        LazyEvaluationStore<TimeSequence> stacks =
                new LazyEvaluationStore<>(OutputterDirectories.STACKS);
        input.stack().addToStore(stacks);

        LazyEvaluationStore<MarkCollection> markss =
                new LazyEvaluationStore<>(OutputterDirectories.MARKS);
        input.marks().addToStore(markss);

        LazyEvaluationStore<KeyValueParams> keyValueParams =
                new LazyEvaluationStore<>("keyValueParams");
        input.keyValueParams().addToStore(keyValueParams);

        LazyEvaluationStore<ObjectCollection> objects =
                new LazyEvaluationStore<>("object-collections");
        input.objects().addToStore(objects);

        MultiCollectionDropDown dropDown =
                new MultiCollectionDropDown(
                        progress -> createTimeSequenceProvider(stacks),
                        markss,
                        objects,
                        keyValueParams,
                        identifier(),
                        true);

        try {
            dropDown.init(globalSubgroupAdder, context, markCreatorParams);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUIWithFile(this, dropDown.openedFileGUI());
    }

    private TimeSequenceProvider createTimeSequenceProvider(
            LazyEvaluationStore<TimeSequence> stacks) throws CreateException {
        try {
            return new TimeSequenceProvider(stacks, input.numberFrames());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
