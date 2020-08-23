/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.threaded.overlay;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.DisplayUpdateRememberStack;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.Redrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedProducer;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedDisplayUpdateConsumer;
import org.anchoranalysis.gui.image.DisplayUpdateCreator;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import lombok.RequiredArgsConstructor;

class ThreadedOverlayUpdateProducer implements Redrawable, ThreadedProducer, IGetClearUpdate {

    private ThreadedDisplayUpdateConsumer consumer;

    private DisplayUpdateCreator displayStackCreator;

    private OverlayedDisplayStackUpdate waitingUpdate = null;
    private PropertyValueChange propertyValueChange = null;
    private MarkDisplaySettingsWrapper markDisplaySettingsWrapper = null;
    private IDGetter<Overlay> idGetter;
    private Logger logger;

    @RequiredArgsConstructor
    private class PropertyValueChange implements PropertyValueChangeListener<MarkDisplaySettings> {

        private final MarkDisplaySettingsWrapper markDisplaySettingsWrapper;

        @Override
        public void propertyValueChanged(PropertyValueChangeEvent<MarkDisplaySettings> evt) {

            DrawOverlay drawOverlay = markDisplaySettingsWrapper.createObjectDrawer();

            try {
                displayStackCreator.updateDrawer(drawOverlay);
                applyRedrawUpdate(OverlayedDisplayStackUpdate.redrawAll());

            } catch (SetOperationFailedException e) {
                logger.errorReporter().recordError(ThreadedOverlayUpdateProducer.class, e);
            }
            consumer.update();
        }
    }

    public ThreadedOverlayUpdateProducer(IDGetter<Overlay> idGetter, Logger logger) {
        this.idGetter = idGetter;
        this.logger = logger;
    }

    public void init(
            final CheckedFunction<Integer, OverlayedDisplayStack, BackgroundStackContainerException>
                    integerToMarksBridge,
            final MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
            int defaultIndex,
            InteractiveThreadPool threadPool,
            final ErrorReporter errorReporter)
            throws InitException {

        propertyValueChange = new PropertyValueChange(markDisplaySettingsWrapper);
        this.markDisplaySettingsWrapper = markDisplaySettingsWrapper;

        // When our Mark display settings change
        markDisplaySettingsWrapper.addChangeListener(propertyValueChange);

        CheckedFunction<Integer, OverlayedDisplayStackUpdate, BackgroundStackContainerException>
                findCorrectUpdate =
                        new FindCorrectUpdate(integerToMarksBridge, () -> consumer != null, this);

        // We create an imageStackGenerator
        // Gives us a generator that works in terms of indexes, rather than Markss
        displayStackCreator =
                setupDisplayUpdateCreator(
                        findCorrectUpdate,
                        idGetter,
                        markDisplaySettingsWrapper.createObjectDrawer());

        consumer =
                new ThreadedDisplayUpdateConsumer(
                        displayStackCreator, defaultIndex, threadPool, errorReporter);
    }

    public OverlayRetriever getOverlayRetriever() {
        return displayStackCreator.getOverlayRetriever();
    }

    // How it provides stacks to other applications (the output)
    @Override
    public DisplayUpdateRememberStack getStackProvider() {
        return consumer;
    }

    // How it is updated with indexes from other classes (the input control mechanism)
    @Override
    public IndexGettableSettable getIndexGettableSettable() {
        return consumer;
    }

    @Override
    public void dispose() {

        if (propertyValueChange != null) {
            assert (markDisplaySettingsWrapper != null);
            markDisplaySettingsWrapper.removeChangeListener(propertyValueChange);
        }

        consumer.dispose();
        consumer = null;
        displayStackCreator = null;
        waitingUpdate = null;
    }

    @Override
    public synchronized void clearWaitingUpdate() {
        waitingUpdate = null;
    }

    @Override
    public synchronized OverlayedDisplayStackUpdate getAndClearWaitingUpdate() {
        OverlayedDisplayStackUpdate update = waitingUpdate;
        waitingUpdate = null;
        return update;
    }

    private void queueWaitingUpdate(OverlayedDisplayStackUpdate update) {
        if (waitingUpdate == null) {
            waitingUpdate = update;
        } else {
            waitingUpdate.mergeWithNewerUpdate(update);
        }
    }

    @Override
    public synchronized void applyRedrawUpdate(OverlayedDisplayStackUpdate update) {
        queueWaitingUpdate(update);
        consumer.update();
    }

    private static DisplayUpdateCreator setupDisplayUpdateCreator(
            CheckedFunction<Integer, OverlayedDisplayStackUpdate, BackgroundStackContainerException>
                    findCorrectUpdate,
            IDGetter<Overlay> idGetter,
            DrawOverlay drawOverlay)
            throws InitException {

        DisplayUpdateCreator displayStackCreator =
                new DisplayUpdateCreator(findCorrectUpdate, idGetter);

        try {
            displayStackCreator.updateDrawer(drawOverlay);
        } catch (SetOperationFailedException e) {
            throw new InitException(e);
        }

        return displayStackCreator;
    }
}
