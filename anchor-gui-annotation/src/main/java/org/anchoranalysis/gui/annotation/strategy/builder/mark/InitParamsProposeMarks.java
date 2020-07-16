/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.videostats.internalframe.annotator.InitParamsWithBackground;

public class InitParamsProposeMarks extends InitParamsWithBackground {

    private AnnotationRefresher annotationRefresher;
    private MarkAnnotator markAnnotator;
    private InitAnnotation initAnnotation;

    public InitParamsProposeMarks(
            AnnotationRefresher annotationRefresher,
            MarkAnnotator markAnnotator,
            AnnotationBackground annotationBackground,
            InitAnnotation initAnnotation) {
        super(annotationBackground);
        this.annotationRefresher = annotationRefresher;
        this.markAnnotator = markAnnotator;
        this.initAnnotation = initAnnotation;
    }

    public AnnotationRefresher getAnnotationRefresher() {
        return annotationRefresher;
    }

    public MarkAnnotator getMarkAnnotator() {
        return markAnnotator;
    }

    public InitAnnotation getInitAnnotation() {
        return initAnnotation;
    }

    public void setInitAnnotation(InitAnnotation initAnnotation) {
        this.initAnnotation = initAnnotation;
    }
}
