/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.videostats.internalframe.annotator;

import static org.anchoranalysis.gui.videostats.internalframe.annotator.FrameActionFactory.*;

import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationWriter;
import org.anchoranalysis.annotation.mark.DualMarks;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.mark.RejectionReason;
import org.anchoranalysis.gui.annotation.save.ISaveAnnotation;

public class SaveActionListenerFactory implements ISaveActionListenerFactory {

    private ISaveAnnotation<DualMarksAnnotation<RejectionReason>> saveAnnotation;
    private DualMarks queryAcceptReject;
    private AnnotationWriterGUI<DualMarksAnnotation<RejectionReason>> annotationWriter;

    public SaveActionListenerFactory(
            ISaveAnnotation<DualMarksAnnotation<RejectionReason>> saveAnnotation,
            DualMarks queryAcceptReject,
            AnnotationRefresher annotationRefresher,
            Optional<SaveMonitor> saveMonitor) {
        super();
        this.saveAnnotation = saveAnnotation;
        this.queryAcceptReject = queryAcceptReject;
        this.annotationWriter =
                new AnnotationWriterGUI<>(
                        new MarkAnnotationWriter<>(), annotationRefresher, saveMonitor);
    }

    @Override
    public ActionListener saveFinished(JInternalFrame frame) {
        return listenerCloseFrame(
                frame,
                () ->
                    saveAnnotation.saveFinished(queryAcceptReject, annotationWriter, frame)
                );
    }

    @Override
    public ActionListener savePaused(JComponent dialogParent) {
        return listener(
                () -> 
                    saveAnnotation.savePaused(queryAcceptReject, annotationWriter, dialogParent)
                );
    }

    @Override
    public ActionListener skipAnnotation(JInternalFrame frame) {
        return listenerCloseFrame(
                frame,
                () -> 
                    saveAnnotation.skipAnnotation(queryAcceptReject, annotationWriter, frame)
                );
    }
}
