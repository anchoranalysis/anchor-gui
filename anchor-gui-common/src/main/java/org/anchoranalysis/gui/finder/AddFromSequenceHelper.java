package org.anchoranalysis.gui.finder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedBiConsumer;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetterFromIndex;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddFromSequenceHelper {

    /**
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

    /**
     * @param <T>
     * @param sequenceType
     * @param getter
     * @param addTo
     * @param namesAsIndexes iff true, we use the indexes as names instead of the existing names
     * @throws E
     */
    public static <T, E extends Exception> void addFromSequenceWithProgressReporter(
            SequenceType sequenceType,
            GetterFromIndex<T> getter,
            CheckedBiConsumer<
                            String, CheckedProgressingSupplier<T, OperationFailedException>, E>
                    addTo,
            boolean namesAsIndexes)
            throws E {
        int min = sequenceType.getMinimumIndex();

        for (int i = min; i != -1; i = sequenceType.nextIndex(i)) {
            String name = sequenceType.indexStr(i);

            final int index = i;
            addTo.accept(
                    nameForKey(namesAsIndexes, index, name),
                    CachedSupplier.cache(() -> {
                        try {
                            return getter.get(index);
                        } catch (GetOperationFailedException e) {
                            throw new OperationFailedException(e);
                        }
                    }).progressing());
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
