/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;

public class AnnotationGuiContext {

    private AnnotationRefresher annotationRefresher;
    private MarkEvaluatorManager markEvaluatorManager;

    public AnnotationGuiContext(
            AnnotationRefresher annotationRefresher, MarkEvaluatorManager markEvaluatorManager) {
        super();
        this.annotationRefresher = annotationRefresher;
        this.markEvaluatorManager = markEvaluatorManager;
    }

    public AnnotationRefresher getAnnotationRefresher() {
        return annotationRefresher;
    }

    public MarkEvaluatorManager getMarkEvaluatorManager() {
        return markEvaluatorManager;
    }
}
