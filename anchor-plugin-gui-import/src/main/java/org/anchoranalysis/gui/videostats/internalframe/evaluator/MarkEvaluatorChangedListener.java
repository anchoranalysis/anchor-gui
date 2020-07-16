/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.util.EventListener;

@FunctionalInterface
public interface MarkEvaluatorChangedListener extends EventListener {
    void markEvaluatorChanged(MarkEvaluatorChangedEvent e);
}
