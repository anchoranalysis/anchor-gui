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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.series.TimeSequenceProviderSupplier;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFactory;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.image.stack.NamedStacksSupplier;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class NRGBackground {

    @Getter private BackgroundSetProgressingSupplier backgroundSet;
    
    @Getter private NRGStackSupplier nrgStack;
    
    private CheckedProgressingSupplier<Integer, OperationFailedException> numberFrames;

    public static NRGBackground createFromBackground(
            BackgroundSetProgressingSupplier backgroundSet,
            NRGStackSupplier nrgStack) {
        return new NRGBackground(backgroundSet, nrgStack, progresssReporter -> 1);
    }

    public static NRGBackground createStack(NamedStacksSupplier backgroundSupplier, NRGStackSupplier nrgStack) {

        return createStackSequence(progressReporter ->
            sequenceProviderFrom(progressReporter, backgroundSupplier),
            nrgStack
        );
    }
    
    public static NRGBackground createStackSequence(
            TimeSequenceProviderSupplier backgroundSet,
            NRGStackSupplier nrgStack) {
        return new NRGBackground(
                CreateBackgroundSetFactory.createCached(backgroundSet),
                nrgStack,
                progress -> {
                    try {
                        return backgroundSet.get(progress).getNumFrames();
                    } catch (Exception e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    public AddVideoStatsModule addNrgStackToAdder(AddVideoStatsModule adder) {
        return new AdderAppendNRGStack(adder, nrgStack::get);
    }

    // Assumes the number of frames does not change
    public NRGBackground copyChangeOp( BackgroundSetProgressingSupplier backgroundSetToAssign) {
        return new NRGBackground(backgroundSetToAssign, nrgStack, numberFrames);
    }

    public int numFrames() throws OperationFailedException {
        return numberFrames.get(ProgressReporterNull.get());
    }

    public String arbitraryBackgroundStackName() throws InitException {
        try {
            return getBackgroundSet().get(ProgressReporterNull.get()).names().iterator().next();

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }

    private static TimeSequenceProvider sequenceProviderFrom(ProgressReporter progressReporter, NamedStacksSupplier backgroundSupplier) throws CreateException {
        try {
            return new TimeSequenceProvider(
                    new WrapStackAsTimeSequence(backgroundSupplier.get(progressReporter)),
                    1
            );
        } catch (OperationFailedException e) {
           throw new CreateException(e);
        }
    }
}
