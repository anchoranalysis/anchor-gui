/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import static org.anchoranalysis.gui.videostats.internalframe.annotator.FrameActionFactory.*;

import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationWriter;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.save.ISaveAnnotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQueryAcceptedRejected;

public class SaveActionListenerFactory<T extends Annotation> implements ISaveActionListenerFactory {

    private ISaveAnnotation<MarkAnnotation> saveAnnotation;
    private IQueryAcceptedRejected queryAcceptReject;
    private AnnotationWriterGUI<MarkAnnotation> annotationWriter;

    public SaveActionListenerFactory(
            ISaveAnnotation<MarkAnnotation> saveAnnotation,
            IQueryAcceptedRejected queryAcceptReject,
            AnnotationRefresher annotationRefresher,
            Optional<SaveMonitor> saveMonitor) {
        super();
        this.saveAnnotation = saveAnnotation;
        this.queryAcceptReject = queryAcceptReject;
        this.annotationWriter =
                new AnnotationWriterGUI<>(
                        new MarkAnnotationWriter(), annotationRefresher, saveMonitor);
    }

    @Override
    public ActionListener saveFinished(JInternalFrame frame) {
        return listenerCloseFrame(
                frame,
                () -> {
                    saveAnnotation.saveFinished(queryAcceptReject, annotationWriter, frame);
                });
    }

    @Override
    public ActionListener savePaused(JComponent dialogParent) {
        return listener(
                () -> {
                    saveAnnotation.savePaused(queryAcceptReject, annotationWriter, dialogParent);
                });
    }

    @Override
    public ActionListener skipAnnotation(JInternalFrame frame) {
        return listenerCloseFrame(
                frame,
                () -> {
                    saveAnnotation.skipAnnotation(queryAcceptReject, annotationWriter, frame);
                });
    }
}
