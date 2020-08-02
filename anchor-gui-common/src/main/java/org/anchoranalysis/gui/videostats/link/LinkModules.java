/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.videostats.link;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.event.IRoutableReceivable;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.PropertyValueReceivableAdapter;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.image.OverlayCollectionWithNrgStack;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule.ReceivableSendablePair;
import org.anchoranalysis.image.stack.DisplayStack;

public class LinkModules {

    private VideoStatsModule module;

    public LinkModules(VideoStatsModule module) {
        super();
        this.module = module;
    }

    private Adder<OverlayCollection> overlays = new Adder<>(LinkFramesUniqueID.OVERLAYS);
    private Adder<OverlayCollectionWithNrgStack> overlaysWithStack =
            new Adder<>(LinkFramesUniqueID.OVERLAYS_WITH_STACK);
    private Adder<Integer> frameIndex = new Adder<>(LinkFramesUniqueID.FRAME_INDEX);
    private Adder<IntArray> markIndices = new Adder<>(LinkFramesUniqueID.MARK_INDICES);
    private Adder<Integer> sliceNum = new Adder<>(LinkFramesUniqueID.SLICE_NUM);
    private Adder<FunctionWithException<Integer, DisplayStack, BackgroundStackContainerException>>
            background = new Adder<>(LinkFramesUniqueID.BACKGROUND);

    public class Adder<T> {

        private String id;

        public Adder(String id) {
            super();
            this.id = id;
        }

        public boolean exists() {
            return module.getReceivableSendablePairMap().get(id) != null;
        }

        @SuppressWarnings("unchecked")
        public IRoutableReceivable<PropertyValueChangeEvent<T>> getReceivable() {
            return module.getReceivableSendablePairMap().get(id).getReceivable();
        }

        public void add(Optional<IPropertyValueReceivable<T>> receiver) {
            internalAdd(receiver, Optional.empty());
        }

        public void add(IPropertyValueSendable<T> sender) {
            internalAdd(Optional.empty(), Optional.of(sender));
        }

        public void add(IRoutableReceivable<PropertyValueChangeEvent<T>> receivable) {
            internalAddRoutable(Optional.of(receivable), Optional.empty());
        }

        public void add(
                Optional<IPropertyValueReceivable<T>> receiver,
                Optional<IPropertyValueSendable<T>> sender) {
            internalAdd(receiver, sender);
        }

        public void add(
                IPropertyValueReceivable<T> receiver,
                IPropertyValueSendable<T> sender,
                Function<PropertyValueChangeEvent<T>, T> funcListener) {
            ReceivableSendablePair<T> rsp = internalAdd(Optional.of(receiver), Optional.of(sender));
            rsp.getReceivable()
                    .addRoutableListener(
                            evt ->
                                    rsp.getSendable()
                                            .setPropertyValue(
                                                    funcListener.apply(evt.getEvent()), false));
        }

        private ReceivableSendablePair<T> internalAdd(
                Optional<IPropertyValueReceivable<T>> receiver,
                Optional<IPropertyValueSendable<T>> sender) {
            ReceivableSendablePair<T> rsp = createPairAdd();
            receiver.ifPresent(
                    r -> rsp.setReceivable(new PropertyValueReceivableAdapter<>(module, r)));
            sender.ifPresent(rsp::setSendable);
            return rsp;
        }

        private ReceivableSendablePair<T> internalAddRoutable(
                Optional<IRoutableReceivable<PropertyValueChangeEvent<T>>> receivable,
                Optional<IPropertyValueSendable<T>> sender) {
            ReceivableSendablePair<T> rsp = createPairAdd();
            receivable.ifPresent(rsp::setReceivable);
            sender.ifPresent(rsp::setSendable);
            return rsp;
        }

        private ReceivableSendablePair<T> createPairAdd() {
            ReceivableSendablePair<T> rsp = new ReceivableSendablePair<>();
            module.getReceivableSendablePairMap().add(id, rsp);
            return rsp;
        }
    }

    public Adder<OverlayCollection> getOverlays() {
        return overlays;
    }

    public Adder<Integer> getFrameIndex() {
        return frameIndex;
    }

    public Adder<IntArray> getMarkIndices() {
        return markIndices;
    }

    public Adder<Integer> getSliceNum() {
        return sliceNum;
    }

    public Adder<OverlayCollectionWithNrgStack> getOverlaysWithStack() {
        return overlaysWithStack;
    }

    public Adder<FunctionWithException<Integer, DisplayStack, BackgroundStackContainerException>>
            getBackground() {
        return background;
    }
}
