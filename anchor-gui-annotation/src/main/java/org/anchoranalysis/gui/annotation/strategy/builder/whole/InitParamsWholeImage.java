/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.videostats.internalframe.annotator.InitParamsWithBackground;

public class InitParamsWholeImage extends InitParamsWithBackground {

    private AnnotationRefresher annotationRefresher;

    public InitParamsWholeImage(
            AnnotationBackground annotationBackground, AnnotationRefresher annotationRefresher) {
        super(annotationBackground);
        this.annotationRefresher = annotationRefresher;
    }

    public AnnotationRefresher getAnnotationRefresher() {
        return annotationRefresher;
    }
}
