/*-
 * #%L
 * anchor-plugin-gui-import
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
import org.anchoranalysis.gui.reassign.FrameTitleCreator;
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

                    delegate.updateState(crntState);
                    updateTitle(index);
                }

            } catch (GetOperationFailedException | StatePanelUpdateException e) {
                errorReporter.recordError(StatePanelFrameHistory.class, e);
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
            this.boundedIndexCntr = selectedHistory.getContainer();
            int actualPhysicalIndex = this.boundedIndexCntr.previousEqualIndex(initialIndex);
            assert (actualPhysicalIndex != -1);
            T startState = this.boundedIndexCntr.get(actualPhysicalIndex);

            this.delegate = new StatePanelFrame<>(title, startState, tablePanel);
            {
                indexSlider =
                        new IndexSlider(
                                selectedHistory.getContainer(), !selectedHistory.isExpensiveLoad());
                indexSlider.setIndex(initialIndex, false);

                indexSlider
                        .getSelectIndexReceivable()
                        .addPropertyValueChangeListener(new IndexChanged());

                delegate.getFrame().add(indexSlider.getSlider(), BorderLayout.SOUTH);
            }

            updateTitle(actualPhysicalIndex);
        } catch (GetOperationFailedException | StatePanelUpdateException e) {
            throw new InitException(e);
        }
    }

    private void updateTitle(int iter) {
        delegate.updateTitle(FrameTitleCreator.title(title, iter));
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
