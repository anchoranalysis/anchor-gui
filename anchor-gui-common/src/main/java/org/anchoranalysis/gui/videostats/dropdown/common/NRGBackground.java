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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFactory;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

@AllArgsConstructor
public class NRGBackground {

    private CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
            opBackgroundSet;
    private CheckedSupplier<NRGStackWithParams, GetOperationFailedException> opNrgStack;
    private CheckedProgressingSupplier<Integer, OperationFailedException> opNumFrames;

    private NRGBackground(
            CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
                    opBackgroundSet,
            CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
                    opNrgStack,
            CheckedProgressingSupplier<Integer, OperationFailedException> opNumFrames) {
        super();
        this.opBackgroundSet = opBackgroundSet;
        this.opNrgStack = removeProgressReporter(opNrgStack);
        this.opNumFrames = opNumFrames;
        assert (opNumFrames != null);
    }

    public static NRGBackground createFromBackground(
            CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
                    opBackgroundSet,
            CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
                    opNrgStack) {
        return new NRGBackground(opBackgroundSet, opNrgStack, progresssReporter -> 1);
    }

    public static <E extends Exception> NRGBackground createStack(
            CheckedProgressingSupplier<NamedProvider<Stack>, E> opBackgroundSet,
            CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
                    opNrgStack) {
        CheckedProgressingSupplier<TimeSequenceProvider, E> opConvert =
                progressReporter ->
                        new TimeSequenceProvider(
                                new WrapStackAsTimeSequence(opBackgroundSet.get(progressReporter)),
                                1);

        return createStackSequence(opConvert, opNrgStack);
    }

    public static NRGBackground createStackSequence(
            CheckedProgressingSupplier<TimeSequenceProvider, ? extends Throwable> opBackgroundSet,
            CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
                    opNrgStack) {
        return new NRGBackground(
                convertProvider(opBackgroundSet),
                opNrgStack,
                progress -> {
                    try {
                        return opBackgroundSet.get(progress).getNumFrames();
                    } catch (Exception e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    public CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
            getBackgroundSet() {
        return opBackgroundSet;
    }

    public IAddVideoStatsModule addNrgStackToAdder(IAddVideoStatsModule adder) {
        return new AdderAppendNRGStack(adder, convertToGetter());
    }

    // Assumes the number of frames does not change
    public NRGBackground copyChangeOp(
            CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
                    opBackgroundSetNew) {
        return new NRGBackground(opBackgroundSetNew, opNrgStack, opNumFrames);
    }

    private INRGStackGetter convertToGetter() {
        return () -> opNrgStack.get();
    }

    private static CheckedSupplier<NRGStackWithParams, GetOperationFailedException>
            removeProgressReporter(
                    CheckedProgressingSupplier<NRGStackWithParams, GetOperationFailedException>
                            in) {
        return () -> {
            return in.get(ProgressReporterNull.get());
        };
    }

    private static CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
            convertProvider(
                    CheckedProgressingSupplier<TimeSequenceProvider, ? extends Throwable> in) {
        return CreateBackgroundSetFactory.createCached(in);
    }

    public CheckedSupplier<NRGStackWithParams, GetOperationFailedException> getNrgStack() {
        return opNrgStack;
    }

    public int numFrames() throws OperationFailedException {
        return opNumFrames.get(ProgressReporterNull.get());
    }

    public String arbitraryBackgroundStackName() throws InitException {
        try {
            return getBackgroundSet().get(ProgressReporterNull.get()).names().iterator().next();

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }
}
