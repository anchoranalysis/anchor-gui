package org.anchoranalysis.plugin.gui.bean.exporttask;

import org.anchoranalysis.io.generator.sequence.OutputSequenceIndexed;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@FunctionalInterface
public interface CreateOutputSequence<T> {
    OutputSequenceIndexed<MappedFrom<T>,Integer> createAndStart(int startIndex) throws OutputWriteFailedException;
}