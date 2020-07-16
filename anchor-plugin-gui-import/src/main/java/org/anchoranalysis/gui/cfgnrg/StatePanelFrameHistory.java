/* (C)2020 */
package org.anchoranalysis.gui.cfgnrg;

import java.awt.BorderLayout;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.image.IndexSlider;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class StatePanelFrameHistory<T> {

    private StatePanelFrame<T> delegate;

    private BoundedIndexContainer<T> boundedIndexCntr;
    private boolean includeIndexAdjusting;
    private IndexSlider indexSlider;
    private String title;
    private ErrorReporter errorReporter;

    private class IndexChanged implements PropertyValueChangeListener<Integer> {

        @Override
        public void propertyValueChanged(PropertyValueChangeEvent<Integer> evt) {

            // We ignore any events which are adjusting, unless includeFrameAdjusting mode is on
            if (!includeIndexAdjusting && evt.getAdjusting()) {
                return;
            }

            try {

                // Our updated state
                {
                    int index = boundedIndexCntr.previousEqualIndex(evt.getValue());
                    T crntState = boundedIndexCntr.get(index);

                    // tablePanel.updateState(evt.getIndexNew(), crnt);
                    delegate.updateState(crntState);
                    updateTitle(index);
                }

            } catch (GetOperationFailedException e) {
                errorReporter.recordError(StatePanelFrameHistory.class, e);
            } catch (StatePanelUpdateException e) {
                errorReporter.recordError(StatePanelFrameHistory.class, e);
                // throw new PropertyValueChangedException(e);
            }
        }
    }

    public StatePanelFrameHistory(String title, boolean includeFrameAdjusting) {
        this.title = title;
        this.includeIndexAdjusting = includeFrameAdjusting;
    }

    public void init(
            int initialIndex,
            LoadContainer<T> selectedHistory,
            StatePanel<T> tablePanel,
            ErrorReporter errorReporter)
            throws InitException {

        this.errorReporter = errorReporter;

        try {
            this.boundedIndexCntr = selectedHistory.getCntr();
            int actualPhysicalIndex = this.boundedIndexCntr.previousEqualIndex(initialIndex);
            assert (actualPhysicalIndex != -1);
            T startState = this.boundedIndexCntr.get(actualPhysicalIndex);

            this.delegate = new StatePanelFrame<>(title, startState, tablePanel);
            {
                indexSlider =
                        new IndexSlider(
                                selectedHistory.getCntr(), !selectedHistory.isExpensiveLoad());
                // frameSlider = new FrameSlider( selectedHistory.getCfgNRGCntnr(), false );
                indexSlider.setIndex(initialIndex, false);

                indexSlider
                        .getSelectIndexReceivable()
                        .addPropertyValueChangeListener(new IndexChanged());

                delegate.getFrame().add(indexSlider.getSlider(), BorderLayout.SOUTH);
            }

            updateTitle(actualPhysicalIndex);
        } catch (GetOperationFailedException e) {
            throw new InitException(e);
        } catch (StatePanelUpdateException e) {
            throw new InitException(e);
        }
    }

    private void updateTitle(int iter) {
        delegate.updateTitle(new FrameTitleGenerator().genTitleString(title, iter));
    }

    public IModuleCreatorDefaultState moduleCreator() {
        return defaultFrameState -> {
            VideoStatsModule module =
                    delegate.moduleCreator().createVideoStatsModule(defaultFrameState);

            IPropertyValueReceivable<Integer> isir =
                    new IPropertyValueReceivable<Integer>() {

                        @Override
                        public void removePropertyValueChangeListener(
                                PropertyValueChangeListener<Integer> changeListener) {
                            indexSlider
                                    .getSelectIndexReceivable()
                                    .removePropertyValueChangeListener(changeListener);
                            delegate.removeFrameChangeListener(changeListener);
                        }

                        @Override
                        public void addPropertyValueChangeListener(
                                PropertyValueChangeListener<Integer> changeListener) {
                            indexSlider
                                    .getSelectIndexReceivable()
                                    .addPropertyValueChangeListener(changeListener);
                            delegate.addFrameChangeListener(changeListener);
                        }
                    };

            LinkModules link = new LinkModules(module);
            link.getFrameIndex()
                    .add(
                            isir,
                            (Integer frameIndex, boolean adjusting) -> {

                                // We ignore any events which are adjusting, unless
                                // includeFrameAdjusting mode is on
                                if (!includeIndexAdjusting && adjusting) {
                                    return;
                                }

                                indexSlider
                                        .getSelectIndexSendable()
                                        .setPropertyValue(frameIndex, adjusting);
                            },
                            // To catch events sent by the panel itself, and route them back to the
                            // index slider
                            PropertyValueChangeEvent::getValue);

            return module;
        };
    }

    public void setFrameSliderVisible(boolean visibility) {
        indexSlider.setVisible(visibility);
    }

    public ControllerSize controllerSize() {
        return delegate.controllerSize();
    }
}
