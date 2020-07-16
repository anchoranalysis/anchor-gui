/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class InitParamsWithBackground extends AnnotationInitParams {

    private AnnotationBackground annotationBackground;

    public InitParamsWithBackground(AnnotationBackground annotationBackground) {
        super();
        this.annotationBackground = annotationBackground;
    }

    public ImageDimensions getDimensionsViewer() {
        return annotationBackground.getDimensionsViewer();
    }

    @Override
    public AnnotationBackground getBackground() {
        return annotationBackground;
    }
}
