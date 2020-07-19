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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSet;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NRGBackground {

    private OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            opBackgroundSet;
    private Operation<NRGStackWithParams, OperationFailedException> opNrgStack;
    private OperationWithProgressReporter<Integer, OperationFailedException> opNumFrames;

    private NRGBackground(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> opBackgroundSet,
            OperationWithProgressReporter<NRGStackWithParams, OperationFailedException> opNrgStack,
            OperationWithProgressReporter<Integer, OperationFailedException> opNumFrames) {
        super();
        this.opBackgroundSet = opBackgroundSet;
        this.opNrgStack = removeProgressReporter(opNrgStack);
        this.opNumFrames = opNumFrames;
        assert (opNumFrames != null);
    }

    public static NRGBackground createFromBackground(
            OperationWithProgressReporter<BackgroundSet, OperationFailedException> opBackgroundSet,
            OperationWithProgressReporter<NRGStackWithParams, OperationFailedException> opNrgStack
    ) {
        return new NRGBackground(
                opBackgroundSet, opNrgStack, new IdentityOperationWithProgressReporter<>(1));
    }

    public static <E extends Exception> NRGBackground createStack(
            OperationWithProgressReporter<NamedProvider<Stack>, E> opBackgroundSet,
            OperationWithProgressReporter<NRGStackWithParams, GetOperationFailedException>
                    opNrgStack) {
        OperationWithProgressReporter<TimeSequenceProvider, E> opConvert =
                progressReporter -> new TimeSequenceProvider(
                    new WrapStackAsTimeSequence(
                        opBackgroundSet.doOperation(progressReporter)),
                        1
                    );
                
        return createStackSequence(opConvert, opNrgStack);
    }

    public static NRGBackground createStackSequence(
            OperationWithProgressReporter<TimeSequenceProvider, ? extends Throwable>
                    opBackgroundSet,
            OperationWithProgressReporter<NRGStackWithParams, OperationFailedException>
                    opNrgStack) {
        return new NRGBackground(
                convertProvider(opBackgroundSet),
                opNrgStack,
                progress -> {
                    try {
                        return opBackgroundSet.doOperation(progress).getNumFrames();
                    } catch (Exception e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    public OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            getBackgroundSet() {
        return opBackgroundSet;
    }

    public IAddVideoStatsModule addNrgStackToAdder(IAddVideoStatsModule adder) {
        return new AdderAppendNRGStack(adder, convertToGetter());
    }

    // Assumes the number of frames does not change
    public NRGBackground copyChangeOp(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    opBackgroundSetNew) {
        return new NRGBackground(opBackgroundSetNew, opNrgStack, opNumFrames);
    }

    private INRGStackGetter convertToGetter() {
        return () -> opNrgStack.doOperation();
    }

    private static Operation<NRGStackWithParams, OperationFailedException> removeProgressReporter(
            OperationWithProgressReporter<NRGStackWithParams, OperationFailedException> in) {
        return () -> {
            return in.doOperation(ProgressReporterNull.get());
        };
    }

    private static OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            convertProvider(
                    OperationWithProgressReporter<TimeSequenceProvider, ? extends Throwable> in) {
        return new OperationCreateBackgroundSet(in);
    }

    public Operation<NRGStackWithParams, OperationFailedException> getNrgStack() {
        return opNrgStack;
    }

    public int numFrames() throws OperationFailedException {
        return opNumFrames.doOperation(ProgressReporterNull.get());
    }

    public String arbitraryBackgroundStackName() throws InitException {
        try {
            return getBackgroundSet()
                    .doOperation(ProgressReporterNull.get())
                    .names()
                    .iterator()
                    .next();

        } catch (GetOperationFailedException e) {
            throw new InitException(e);
        }
    }
}
