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

package org.anchoranalysis.gui.backgroundset;

import java.util.Set;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CallableWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.container.background.SingleBackgroundStackCntr;
import org.anchoranalysis.gui.serializedobjectset.MarkWithRaster;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.experiment.identifiers.StackIdentifiers;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BackgroundSetFactory {

    public static BackgroundSet createBackgroundSet(
            NamedProvider<TimeSequence> stacks, ProgressReporter progressReporter)
            throws CreateException {
        BackgroundSet set = new BackgroundSet();
        try {
            addFromStacks(set, stacks, progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
        return set;
    }

    public static BackgroundSet createBackgroundSetFromExisting(
            BackgroundSet existing,
            NamedProvider<TimeSequence> stacks,
            ProgressReporter progressReporter)
            throws CreateException {

        BackgroundSet set = new BackgroundSet();
        set.addAll(existing);
        try {
            BackgroundSetFactory.addFromStacks(
                    set, stacks, progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return set;
    }

    public static BackgroundSet createBackgroundSetFromExisting(
            BackgroundSet existing,
            NamedProvider<TimeSequence> stacks,
            Set<String> keys,
            ProgressReporter progressReporter)
            throws CreateException {

        BackgroundSet bsNew = new BackgroundSet();
        bsNew.addAll(existing);
        try {
            BackgroundSetFactory.addFromStacks(
                    bsNew, stacks, keys, progressReporter);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return bsNew;
    }

    public static BackgroundSet createMergedBackgroundSet(LoadContainer<MarkWithRaster> lc)
            throws GetOperationFailedException {
        BackgroundSet backgroundSet = new BackgroundSet();

        // We assume every LoadContainer contains the same rasters in the BackgroundSet
        ///  and use the first one to get the names

        BackgroundSet first = lc.getCntr().get(lc.getCntr().getMinimumIndex()).getBackgroundSet();

        for (String name : first.names()) {
            backgroundSet.addItem(name, new SingleBackgroundStackCntr(rasterBridge(lc, name)));
        }

        return backgroundSet;
    }

    private static BoundedIndexContainer<DisplayStack> rasterBridge(
            final LoadContainer<MarkWithRaster> cntr, final String name) {

        assert (cntr != null);
        return new BoundedIndexContainerBridgeWithoutIndex<>(
                cntr.getCntr(),
                sourceObject -> {
                    assert (sourceObject != null);
                    return sourceObject.getBackgroundSet().singleStack(name);
                });
    }

    @AllArgsConstructor
    private static class AddBackgroundSetItem implements CallableWithException<BackgroundStackContainer, BackgroundStackContainerException> {

        private NamedProvider<TimeSequence> imageStackCollection;
        private String id;

        @Override
        public BackgroundStackContainer call() throws BackgroundStackContainerException {

            try {
                TimeSequence seq = imageStackCollection.getException(id);

                if (seq.size() > 1) {
                    return createBackgroundTimeSeries(seq);
                } else {
                    return createBackgroundNotTimeSeries(seq.get(0));
                }

            } catch (NamedProviderGetException | OperationFailedException e) {
                throw new BackgroundStackContainerException(e);
            }
        }
    }

    private static BackgroundStackContainer createBackgroundTimeSeries(TimeSequence seq)
            throws OperationFailedException {
        return BackgroundStackContainerFactory.convertedSequence(seq);
    }

    private static BackgroundStackContainer createBackgroundNotTimeSeries(Stack img)
            throws OperationFailedException {
        return BackgroundStackContainerFactory.singleSavedStack(img);
    }

    private static void addFromStacks(
            BackgroundSet backgroundSet,
            NamedProvider<TimeSequence> imageStackCollection,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        addFromStacks(
                backgroundSet, imageStackCollection, imageStackCollection.keys(), progressReporter);

        addEmpty(backgroundSet, imageStackCollection);
    }

    public static void addEmpty(
            BackgroundSet backgroundSet, NamedProvider<TimeSequence> imageStackCollection) {

        backgroundSet.addItem(
                "blank (all black)",
                () -> {
                    try {
                        ImageDimensions sd = guessDimensions(imageStackCollection);
                        Stack stack = createEmptyStack(sd);
                        return BackgroundStackContainerFactory.singleSavedStack(stack);
                    } catch (OperationFailedException e) {
                        throw new BackgroundStackContainerException("blank", e);
                    }
                });
    }

    private static ImageDimensions guessDimensions(NamedProvider<TimeSequence> imageStackCollection)
            throws OperationFailedException {
        try {
            return imageStackCollection
                    .getException(imageStackCollection.keys().iterator().next())
                    .getDimensions();
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    private static Stack createEmptyStack(ImageDimensions dimensions)
            throws OperationFailedException {
        try {
            Stack stack = new Stack();
            stack.addChannel(
                    ChannelFactory.instance()
                            .createEmptyInitialised(
                                    dimensions, VoxelDataTypeUnsignedByte.INSTANCE));
            return stack;
        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }

    private static void addFromStacks(
            BackgroundSet backgroundSet,
            NamedProvider<TimeSequence> namedStacks,
            Set<String> keys,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        boolean hasNrgStack = keys.contains(StackIdentifiers.NRG_STACK);

        ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter);
        pri.setMin(0);
        pri.setMax(keys.size() + (hasNrgStack ? 1 : 0));

        pri.open();

        try {

            // The way we handle this means we cannot add the (only first three) brackets on the
            // name, as the image has not yet been evaluated
            for (String id : keys) {
                CallableWithException<BackgroundStackContainer, BackgroundStackContainerException> operation = CachedOperation.of(
                        new AddBackgroundSetItem(namedStacks, id)
                );
                backgroundSet.addItem(id, operation);
                pri.update();
            }

            // TODO fix, this will always evaluate the NRG stack
            // We add each part of the NRG Stack separately

            if (hasNrgStack) {
                addStackAsSeparateChnl(
                        backgroundSet,
                        namedStacks
                                .getException(StackIdentifiers.NRG_STACK)
                                .get(0), // Only take first
                        "nrgStack-chnl");
                pri.update();
            }

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        } finally {
            pri.close();
        }
    }

    private static void addStackAsSeparateChnl(
            BackgroundSet backgroundSet, Stack stack, String prefix)
            throws OperationFailedException {

        try {
            for (int c = 0; c < stack.getNumberChannels(); c++) {
                // We create a stack just with this channel
                Stack stackSingle = new Stack();
                stackSingle.addChannel(stack.getChannel(c));

                backgroundSet.addItem(String.format("%s%d", prefix, c), stackSingle);
            }
        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }
}
