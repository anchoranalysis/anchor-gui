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

package org.anchoranalysis.gui.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.annotation.io.AnnotationWithStrategy;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilderFactory;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

// A set of annotations
public class AnnotationProject {

    private List<FileAnnotationNamedChannels> list = new ArrayList<>();
    private EventListenerList eventListenerList = new EventListenerList();

    private class RefreshAndTriggerEvent implements AnnotationRefresher {

        private int index;

        public RefreshAndTriggerEvent(int index) {
            super();
            this.index = index;
        }

        @Override
        public void refreshAnnotation() {

            for (AnnotationChangedListener l :
                    eventListenerList.getListeners(AnnotationChangedListener.class)) {
                l.annotationChanged(index);
            }
        }
    }

    public <T extends AnnotatorStrategy> AnnotationProject(
            Collection<AnnotationWithStrategy<T>> collInputs,
            MarkEvaluatorManager markEvaluatorManager,
            VideoStatsModuleGlobalParams mpg,
            ProgressReporter progressReporter)
            throws CreateException {
        List<AnnotationWithStrategy<T>> listTemp = new ArrayList<>();

        for (AnnotationWithStrategy<T> input : collInputs) {
            listTemp.add(input);
        }

        progressReporter.setMin(0);
        progressReporter.setMax(listTemp.size() - 1);
        progressReporter.open();

        try {
            for (int i = 0; i < listTemp.size(); i++) {

                AnnotationWithStrategy<?> obj = listTemp.get(i);

                list.add(
                        new FileAnnotationNamedChannels(
                                AnnotationGuiBuilderFactory.create(obj),
                                new RefreshAndTriggerEvent(i),
                                markEvaluatorManager,
                                mpg));

                progressReporter.update(i);
            }

        } finally {
            progressReporter.close();
        }
    }

    public FileAnnotationNamedChannels get(int arg0) {
        return list.get(arg0);
    }

    public int size() {
        return list.size();
    }

    public int numAnnotated() {
        int cnt = 0;
        for (FileAnnotationNamedChannels item : list) {
            if (item.summary().isExistsFinished()) {
                cnt++;
            }
        }
        return cnt;
    }

    public void addAnnotationChangedListener(AnnotationChangedListener l) {
        eventListenerList.add(AnnotationChangedListener.class, l);
    }
}
