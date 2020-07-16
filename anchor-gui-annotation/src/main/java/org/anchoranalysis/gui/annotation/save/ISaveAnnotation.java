/* (C)2020 */
package org.anchoranalysis.gui.annotation.save;

import javax.swing.JComponent;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQueryAcceptedRejected;

public interface ISaveAnnotation<T extends Annotation> {

    void saveFinished(
            IQueryAcceptedRejected query,
            AnnotationWriterGUI<T> annotationWriter,
            JComponent dialogParent);

    void savePaused(
            IQueryAcceptedRejected query,
            AnnotationWriterGUI<T> annotationWriter,
            JComponent dialogParent);

    void skipAnnotation(
            IQueryAcceptedRejected query,
            AnnotationWriterGUI<T> annotationWriter,
            JComponent dialogParent);
}
