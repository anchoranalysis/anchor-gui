/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.overlay;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.frame.threaded.stack.IThreadedProducer;
import org.anchoranalysis.gui.frame.threaded.stack.ThreadedDisplayUpdateConsumer;
import org.anchoranalysis.gui.image.DisplayUpdateCreator;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;

class ThreadedOverlayUpdateProducer implements IRedrawable, IThreadedProducer, IGetClearUpdate {

    private ThreadedDisplayUpdateConsumer consumer;

    private DisplayUpdateCreator displayStackCreator;

    private OverlayedDisplayStackUpdate waitingUpdate = null;
    private PropertyValueChange propertyValueChange = null;
    private MarkDisplaySettingsWrapper markDisplaySettingsWrapper = null;
    private IDGetter<Overlay> idGetter;
    private Logger logger;

    private class PropertyValueChange implements PropertyValueChangeListener<MarkDisplaySettings> {

        private final MarkDisplaySettingsWrapper markDisplaySettingsWrapper;

        public PropertyValueChange(MarkDisplaySettingsWrapper markDisplaySettingsWrapper) {
            super();
            this.markDisplaySettingsWrapper = markDisplaySettingsWrapper;
        }

        @Override
        public void propertyValueChanged(PropertyValueChangeEvent<MarkDisplaySettings> evt) {

            DrawOverlay maskWriter = markDisplaySettingsWrapper.createObjectDrawer();

            try {
                displayStackCreator.updateMaskWriter(maskWriter);
                applyRedrawUpdate(OverlayedDisplayStackUpdate.redrawAll());

                // cfgGenerator.applyRedrawUpdate( new ColoredCfgRedrawUpdate(null) );
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
            final FunctionWithException<Integer, OverlayedDisplayStack, GetOperationFailedException>
                    integerToCfgBridge,
            final MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
            int defaultIndex,
            InteractiveThreadPool threadPool,
            final ErrorReporter errorReporter)
            throws InitException {

        propertyValueChange = new PropertyValueChange(markDisplaySettingsWrapper);
        this.markDisplaySettingsWrapper = markDisplaySettingsWrapper;

        // When our Mark display settings change
        markDisplaySettingsWrapper.addChangeListener(propertyValueChange);

        FunctionWithException<Integer, OverlayedDisplayStackUpdate, GetOperationFailedException>
                findCorrectUpdate =
                        new FindCorrectUpdate(integerToCfgBridge, () -> consumer != null, this);

        // We create an imageStackGenerator
        // Gives us a generator that works in terms of indexes, rather than Cfgs
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
    public IDisplayUpdateRememberStack getStackProvider() {
        return consumer;
    }

    // How it is updated with indexes from other classes (the input control mechanism)
    @Override
    public IIndexGettableSettable getIndexGettableSettable() {
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
            FunctionWithException<Integer, OverlayedDisplayStackUpdate, GetOperationFailedException>
                    findCorrectUpdate,
            IDGetter<Overlay> idGetter,
            DrawOverlay maskWriter)
            throws InitException {

        DisplayUpdateCreator displayStackCreator =
                new DisplayUpdateCreator(findCorrectUpdate, idGetter);

        try {
            displayStackCreator.updateMaskWriter(maskWriter);
        } catch (SetOperationFailedException e) {
            throw new InitException(e);
        }

        return displayStackCreator;
    }
}
