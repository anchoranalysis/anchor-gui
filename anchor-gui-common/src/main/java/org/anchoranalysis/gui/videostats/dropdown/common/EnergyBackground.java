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

package org.anchoranalysis.gui.videostats.dropdown.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.series.TimeSequenceProviderSupplier;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendEnergyStack;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFactory;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.image.core.stack.time.WrapStackAsTimeSequence;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EnergyBackground {

    @Getter private BackgroundSetProgressingSupplier backgroundSet;

    @Getter private EnergyStackSupplier energyStack;

    private CheckedProgressingSupplier<Integer, OperationFailedException> numberFrames;

    public static EnergyBackground createFromBackground(
            BackgroundSetProgressingSupplier backgroundSet, EnergyStackSupplier energyStack) {
        return new EnergyBackground(backgroundSet, energyStack, progresssReporter -> 1);
    }

    public static EnergyBackground createStack(
            NamedStacksSupplier backgroundSupplier, EnergyStackSupplier energyStack) {

        return createStackSequence(
                progress -> sequenceProviderFrom(progress, backgroundSupplier), energyStack);
    }

    public static EnergyBackground createStackSequence(
            TimeSequenceProviderSupplier backgroundSet, EnergyStackSupplier energyStack) {
        return new EnergyBackground(
                CreateBackgroundSetFactory.createCached(backgroundSet),
                energyStack,
                progress -> {
                    try {
                        return backgroundSet.get(progress).getNumFrames();
                    } catch (Exception e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    public AddVideoStatsModule addEnergyStackToAdder(AddVideoStatsModule adder) {
        return new AdderAppendEnergyStack(adder, energyStack::get);
    }

    // Assumes the number of frames does not change
    public EnergyBackground copyChangeOp(BackgroundSetProgressingSupplier backgroundSetToAssign) {
        return new EnergyBackground(backgroundSetToAssign, energyStack, numberFrames);
    }

    public int numberFrames() throws OperationFailedException {
        return numberFrames.get(ProgressIgnore.get());
    }

    public String arbitraryBackgroundStackName() throws InitException {
        try {
            return getBackgroundSet().get(ProgressIgnore.get()).names().iterator().next();

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }

    private static TimeSequenceProvider sequenceProviderFrom(
            Progress progress, NamedStacksSupplier backgroundSupplier) throws CreateException {
        try {
            return new TimeSequenceProvider(
                    new WrapStackAsTimeSequence(backgroundSupplier.get(progress)), 1);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
