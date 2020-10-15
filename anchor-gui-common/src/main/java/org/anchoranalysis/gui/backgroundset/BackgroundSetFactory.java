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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.dimensions.Dimensions;
import org.anchoranalysis.image.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.experiment.identifiers.StackIdentifiers;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

        BackgroundSet set = new BackgroundSet(existing);
        try {
            BackgroundSetFactory.addFromStacks(set, stacks, progressReporter);
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

        BackgroundSet backgroundSetNew = new BackgroundSet(existing);
        try {
            BackgroundSetFactory.addFromStacks(backgroundSetNew, stacks, keys, progressReporter);
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
            ProgressReporter progressReporter)
            throws OperationFailedException {
        addFromStacks(
                backgroundSet, imageStackCollection, imageStackCollection.keys(), progressReporter);

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
            ProgressReporter progressReporter)
            throws OperationFailedException {

        boolean hasEnergyStack = keys.contains(StackIdentifiers.ENERGY_STACK);

        ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter);
        pri.setMin(0);
        pri.setMax(keys.size() + (hasEnergyStack ? 1 : 0));

        pri.open();

        try {

            // The way we handle this means we cannot add the (only first three) brackets on the
            // name, as the image has not yet been evaluated
            for (String id : keys) {
                BackgroundSetSupplier<BackgroundStackContainer> operation =
                        CachedSupplier.cache(() -> addBackgroundSetItem(namedStacks, id))::get;
                backgroundSet.addItem(id, operation);
                pri.update();
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
                pri.update();
            }

        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        } finally {
            pri.close();
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
