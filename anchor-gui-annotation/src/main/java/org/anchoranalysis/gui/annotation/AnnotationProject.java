/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilderFactory;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

// A set of annotations
public class AnnotationProject {

    private List<FileAnnotationNamedChnlCollection> list = new ArrayList<>();
    private EventListenerList eventListenerList = new EventListenerList();

    private class RefreshAndTriggerEvent implements AnnotationRefresher {

        private int index;

        public RefreshAndTriggerEvent(int index) {
            super();
            this.index = index;
        }

        @Override
        public void refreshAnnotation() {

            // list.get(index).refresh();

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
                        new FileAnnotationNamedChnlCollection(
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

    public FileAnnotationNamedChnlCollection get(int arg0) {
        return list.get(arg0);
    }

    public int size() {
        return list.size();
    }

    public int numAnnotated() {
        int cnt = 0;
        for (FileAnnotationNamedChnlCollection item : list) {
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
