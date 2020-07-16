/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;

@FunctionalInterface
public interface EvaluatorWithContextGetter {
    public Optional<EvaluatorWithContext> getEvaluatorWithContext()
            throws GetOperationFailedException;
}
