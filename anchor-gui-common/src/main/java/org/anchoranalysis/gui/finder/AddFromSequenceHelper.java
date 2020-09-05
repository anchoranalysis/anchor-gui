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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedBiConsumer;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddFromSequenceHelper {

    /**
     * Adds from a sequence
     *
     * @param <T>
     * @param sequenceType
     * @param getter
     * @param addTo
     * @param namesAsIndexes iff true, we use the indexes as names instead of the existing names
     * @throws E
     */
    public static <T, E extends Exception> void addFromSequence(
            SequenceType sequenceType,
            GetterFromIndex<T> getter,
            CheckedBiConsumer<String, StoreSupplier<T>, E> addTo,
            boolean namesAsIndexes)
            throws E {
        int min = sequenceType.getMinimumIndex();

        for (int i = min; i != -1; i = sequenceType.nextIndex(i)) {
            String name = sequenceType.indexStr(i);

            final int index = i;
            addTo.accept(
                    nameForKey(namesAsIndexes, index, name),
                    StoreSupplier.cache(
                            () -> {
                                try {
                                    return getter.get(index);
                                } catch (GetOperationFailedException e) {
                                    throw new OperationFailedException(e);
                                }
                            }));
        }
    }

    private static String nameForKey(boolean namesAsIndexes, int index, String name) {
        if (namesAsIndexes) {
            return String.valueOf(index);
        } else {
            return name;
        }
    }
}
