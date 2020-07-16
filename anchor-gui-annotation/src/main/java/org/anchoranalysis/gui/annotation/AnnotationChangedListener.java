/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.util.EventListener;

public interface AnnotationChangedListener extends EventListener {

    void annotationChanged(int index);
}
