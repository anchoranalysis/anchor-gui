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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIncrement;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.StackIdentifiers;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackgroundSetFactory {

    public static BackgroundSet createBackgroundSet(
            NamedProvider<TimeSequence> stacks, Progress progress) throws CreateException {
        BackgroundSet set = new BackgroundSet();
        try {
            addFromStacks(set, stacks, progress);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
        return set;
    }

    public static BackgroundSet createBackgroundSetFromExisting(
            BackgroundSet existing, NamedProvider<TimeSequence> stacks, Progress progress)
            throws CreateException {

        BackgroundSet set = new BackgroundSet(existing);
        try {
            BackgroundSetFactory.addFromStacks(set, stacks, progress);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return set;
    }

    public static BackgroundSet createBackgroundSetFromExisting(
            BackgroundSet existing,
            NamedProvider<TimeSequence> stacks,
            Set<String> keys,
            Progress progress)
            throws CreateException {

        BackgroundSet backgroundSetNew = new BackgroundSet(existing);
        try {
            BackgroundSetFactory.addFromStacks(backgroundSetNew, stacks, keys, progress);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return backgroundSetNew;
    }

    private static BackgroundStackContainer addBackgroundSetItem(
            NamedProvider<TimeSequence> imageStackCollection, String id)
            throws BackgroundStackContainerException {

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
            Progress progress)
            throws OperationFailedException {
        addFromStacks(backgroundSet, imageStackCollection, imageStackCollection.keys(), progress);

        addEmpty(backgroundSet, imageStackCollection);
    }

    public static void addEmpty(BackgroundSet backgroundSet, NamedProvider<TimeSequence> stacks) {

        backgroundSet.addItem(
                "blank (all black)",
                () -> {
                    try {
                        Stack stack = createEmptyStack(guessDimensions(stacks));
                        return BackgroundStackContainerFactory.singleSavedStack(stack);
                    } catch (OperationFailedException e) {
                        throw new BackgroundStackContainerException("blank", e);
                    }
                });
    }

    private static Dimensions guessDimensions(NamedProvider<TimeSequence> imageStackCollection)
            throws OperationFailedException {
        try {
            return imageStackCollection
                    .getException(imageStackCollection.keys().iterator().next())
                    .dimensions();
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }

    private static Stack createEmptyStack(Dimensions dimensions) throws OperationFailedException {
        try {
            Stack stack = new Stack();
            stack.addChannel(
                    ChannelFactory.instance().create(dimensions, UnsignedByteVoxelType.INSTANCE));
            return stack;
        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }

    private static void addFromStacks(
            BackgroundSet backgroundSet,
            NamedProvider<TimeSequence> namedStacks,
            Set<String> keys,
            Progress progress)
            throws OperationFailedException {

        boolean hasEnergyStack = keys.contains(StackIdentifiers.ENERGY_STACK);

        ProgressIncrement progressIncrement = new ProgressIncrement(progress);
        progressIncrement.setMin(0);
        progressIncrement.setMax(keys.size() + (hasEnergyStack ? 1 : 0));

        progressIncrement.open();

        try {

            // The way we handle this means we cannot add the (only first three) brackets on the
            // name, as the image has not yet been evaluated
            for (String id : keys) {
                BackgroundSetSupplier<BackgroundStackContainer> operation =
                        CachedSupplier.cache(() -> addBackgroundSetItem(namedStacks, id))::get;
                backgroundSet.addItem(id, operation);
                progressIncrement.update();
            }

            // TODO fix, this will always evaluate the energy stack
            // We add each part of the energy stack separately

            if (hasEnergyStack) {
                addStackAsSeparateChannel(
                        backgroundSet,
                        namedStacks
                                .getException(StackIdentifiers.ENERGY_STACK)
                                .get(0), // Only take first
                        "energyStack-channel");
                progressIncrement.update();
            }

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        } finally {
            progressIncrement.close();
        }
    }

    private static void addStackAsSeparateChannel(
            BackgroundSet backgroundSet, Stack stack, String prefix)
            throws OperationFailedException {

        for (int index = 0; index < stack.getNumberChannels(); index++) {
            // We create a stack just with this channel
            String name = String.format("%s%d", prefix, index);
            Stack stackSingle = new Stack(stack.getChannel(index));
            backgroundSet.addItem(name, stackSingle);
        }
    }
}
