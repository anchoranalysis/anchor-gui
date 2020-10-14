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

package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.serialize.XStreamDeserializer;
import org.anchoranalysis.gui.finder.AddFromSequenceHelper;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;
import org.anchoranalysis.io.manifest.directory.sequenced.SequencedDirectoryDeserializer;
import org.anchoranalysis.io.manifest.finder.FinderSingleDirectory;
import org.anchoranalysis.io.manifest.finder.match.DirectoryMatch;
import org.anchoranalysis.mpp.mark.MarkCollection;

@RequiredArgsConstructor
public class FinderMarksFolder extends FinderSingleDirectory {

    private static final Deserializer<MarkCollection> DESERIALIZER = new XStreamDeserializer<>();
    
    // START REQUIRED ARGUMENTS
    private final String manifestFunction;
    private final String directoryName;
    // END REQUIRED ARGUMENTS

    public NamedProvider<MarkCollection> createNamedProvider(boolean namesAsIndexes)
            throws OperationFailedException {

        if (!exists()) {
            return new NameValueSet<>();
        }

        LazyEvaluationStore<MarkCollection> out = new LazyEvaluationStore<>("finderMarksFolder");

        SequencedDirectoryDeserializer<MarkCollection> deserializer =
                new SequencedDirectoryDeserializer<>(getFoundDirectory(), DESERIALIZER);

        AddFromSequenceHelper.addFromSequence(
                getFoundDirectory().getAssociatedElementRange(), deserializer, out::add, namesAsIndexes);
        return out;
    }

    @Override
    protected Predicate<MutableDirectory> matchDirectories() {
        return DirectoryMatch.path(directoryName).and(DirectoryMatch.description(this.manifestFunction, "serialized"));
    }
}
