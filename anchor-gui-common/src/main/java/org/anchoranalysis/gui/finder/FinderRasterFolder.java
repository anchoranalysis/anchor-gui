/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.finder;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.core.index.bounded.BoundsFromRange;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;
import org.anchoranalysis.io.manifest.finder.FinderSingleDirectory;
import org.anchoranalysis.io.manifest.finder.match.DirectoryMatch;

@RequiredArgsConstructor
public class FinderRasterFolder extends FinderSingleDirectory {

    // START: REQUIRED ARGUMENTS
    private final String directoryName;
    private final String manifestFunction;
    private final StackReader stackReader;
    // END: REQUIRED ARGUMENTS

    private BoundedIndexContainer<Stack> result;

    public BoundedIndexContainer<Stack> get() {
        assert (exists());
        if (result == null) {
            result = createContainer(getFoundDirectory());
        }
        return result;
    }

    // If namesAsIndexes is true, we use the indexes as names instead of the existing names
    public NamedStacks createStackCollection(boolean namesAsIndexes) {

        if (getFoundDirectory() == null) {
            return new NamedStacks();
        }

        NamedStacks stacks = new NamedStacks();

        SequencedDirectoryStackReader reader =
                new SequencedDirectoryStackReader(getFoundDirectory(), stackReader);

        AddFromSequenceHelper.addFromSequence(
                getFoundDirectory().getAssociatedElementRange(),
                reader,
                stacks::add,
                namesAsIndexes);

        return stacks;
    }

    @Override
    protected Predicate<MutableDirectory> matchDirectories() {
        return DirectoryMatch.path(directoryName)
                .and(DirectoryMatch.description(this.manifestFunction, "raster"));
    }

    private BoundedIndexContainer<Stack> createContainer(MutableDirectory folder) {
        return new BoundsFromRange<>(
                new SequencedDirectoryStackReader(folder, stackReader),
                folder.getAssociatedElementRange());
    }
}
