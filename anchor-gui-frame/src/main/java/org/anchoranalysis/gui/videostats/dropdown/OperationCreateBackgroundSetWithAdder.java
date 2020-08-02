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

package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.videostats.dropdown.addoverlays.AdderAddOverlaysWithStack;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackgroundAdder;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.image.experiment.identifiers.StackIdentifiers;
import org.anchoranalysis.image.stack.DisplayStack;

public class OperationCreateBackgroundSetWithAdder
        extends CachedOperationWithProgressReporter<
                BackgroundSetWithAdder, BackgroundStackContainerException> {

    private IAddVideoStatsModule parentAdder;
    private InteractiveThreadPool threadPool;
    private ErrorReporter errorReporter;

    private NRGBackground nrgBackground;
    private NRGBackgroundAdder<BackgroundStackContainerException> nrgBackgroundNew;

    private CachedOperationWithProgressReporter<IAddVideoStatsModule, BackgroundStackContainerException>
            operationIAddVideoStatsModule =
                    new WrapOperationWithProgressReporterAsCached<>(
                            progressReporter -> {
                                BackgroundSetWithAdder bwsa =
                                        OperationCreateBackgroundSetWithAdder.this.doOperation(
                                                progressReporter);
                                return bwsa.getAdder();
                            });

    private CachedOperationWithProgressReporter<BackgroundSet, BackgroundStackContainerException>
            operationBackgroundSet =
                    new WrapOperationWithProgressReporterAsCached<>(
                            progressReporter -> {
                                BackgroundSetWithAdder bwsa =
                                        OperationCreateBackgroundSetWithAdder.this.doOperation(
                                                progressReporter);
                                return bwsa.getBackgroundSet();
                            });

    public OperationCreateBackgroundSetWithAdder(
            NRGBackground nrgBackground,
            IAddVideoStatsModule parentAdder,
            InteractiveThreadPool threadPool,
            ErrorReporter errorReporter) {
        super();
        this.nrgBackground = nrgBackground;
        this.parentAdder = parentAdder;
        this.threadPool = threadPool;
        this.errorReporter = errorReporter;

        // A new nrgBackground that includes the changed operation for the background
        this.nrgBackgroundNew =
                new NRGBackgroundAdder<>(
                        nrgBackground.copyChangeOp(operationBackgroundSet),
                        operationIAddVideoStatsModule);
    }

    @Override
    protected BackgroundSetWithAdder execute(ProgressReporter progressReporter) throws BackgroundStackContainerException {
        BackgroundSetWithAdder bwsa = new BackgroundSetWithAdder();

        BackgroundSet backgroundSet;
        backgroundSet = nrgBackground.getBackgroundSet().doOperation(progressReporter);
        
        bwsa.setBackgroundSet(backgroundSet);

        IAddVideoStatsModule childAdder = parentAdder.createChild();
        childAdder = new AdderAddOverlaysWithStack(childAdder, threadPool, errorReporter);

        childAdder = nrgBackground.addNrgStackToAdder(childAdder);

        FunctionWithException<Integer, DisplayStack, BackgroundStackContainerException> initialBackground;
        try {
            initialBackground = initialBackground(backgroundSet);
        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }


        // TODO For now we assume there is always an index 0 available as a minimum
        DisplayStack initialStack = initialBackground.apply(0);


        // TODO is this the right place?
        // Sets an appropriate default slice in the middle
        assignDefaultSliceForStack(childAdder, initialStack);

        bwsa.setAdder(childAdder);

        childAdder
                .getSubgroup()
                .getDefaultModuleState()
                .getLinkStateManager()
                .setBackground(initialBackground);

        return bwsa;
    }

    public OperationWithProgressReporter<IAddVideoStatsModule, BackgroundStackContainerException>
            operationAdder() {
        return operationIAddVideoStatsModule;
    }

    public NRGBackgroundAdder<BackgroundStackContainerException> nrgBackground() {
        return nrgBackgroundNew;
    }
    
    private static FunctionWithException<Integer, DisplayStack, BackgroundStackContainerException>
            initialBackground(BackgroundSet backgroundSet) throws GetOperationFailedException {

        if (backgroundSet.names().contains(StackIdentifiers.INPUT_IMAGE)) {
            return backgroundSet.stackCntr(StackIdentifiers.INPUT_IMAGE);
        } else {
            String first = backgroundSet.names().iterator().next();
            return backgroundSet.stackCntr(first);
        }
    }

    private static void assignDefaultSliceForStack(
            IAddVideoStatsModule childAdder, DisplayStack stack) {

        IPropertyValueSendable<Integer> sliceNumSetter =
                childAdder
                        .getSubgroup()
                        .getDefaultModuleState()
                        .getLinkStateManager()
                        .getSendableSliceNum();
        if (sliceNumSetter != null) {
            sliceNumSetter.setPropertyValue(stack.getDimensions().getZ() / 2, false);
        }
    }
}
