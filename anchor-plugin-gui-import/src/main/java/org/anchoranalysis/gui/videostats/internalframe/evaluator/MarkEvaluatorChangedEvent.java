/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;

public class MarkEvaluatorChangedEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;

    private MarkEvaluatorResolved markEvalutor;
    private String markEvaluatorName;

    public MarkEvaluatorChangedEvent(
            Object source, MarkEvaluatorResolved markEvalutor, String markEvaluatorName) {
        super(source);
        this.markEvalutor = markEvalutor;
        this.markEvaluatorName = markEvaluatorName;
    }

    public MarkEvaluatorResolved getMarkEvaluator() {
        return markEvalutor;
    }

    public String getMarkEvaluatorName() {
        return markEvaluatorName;
    }
}
