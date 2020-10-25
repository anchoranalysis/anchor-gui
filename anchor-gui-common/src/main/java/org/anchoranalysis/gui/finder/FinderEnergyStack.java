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

package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyStackSupplier;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.io.manifest.finder.FinderKeyValueParams;

public class FinderEnergyStack implements Finder {

    private FinderRasterFolder finderRasterFolder;
    private EnergyStackSupplier energyStackSupplier;
    private FinderKeyValueParams finderImageParams;

    private NamedStacksSupplier operationStacks;

    public FinderEnergyStack(StackReader stackReader, ErrorReporter errorReporter) {
        finderRasterFolder = new FinderRasterFolder("energyStack", "energyStack", stackReader);

        this.operationStacks =
                NamedStacksSupplier.cache(
                        progressReporter -> finderRasterFolder.createStackCollection(true));

        this.finderImageParams = new FinderKeyValueParams("energyStackParams", errorReporter);

        this.energyStackSupplier = EnergyStackSupplier.cache(this::findStack);
    }

    @Override
    public boolean doFind(Manifest manifestRecorder) {
        finderImageParams.doFind(manifestRecorder);
        return finderRasterFolder.doFind(manifestRecorder);
    }

    @Override
    public boolean exists() {
        return finderRasterFolder.exists();
    }

    public NamedProvider<Stack> getNamedStacks() throws OperationFailedException {
        return operationStacks.get(ProgressReporterNull.get());
    }

    public EnergyStackSupplier energyStackSupplier() {
        return energyStackSupplier;
    }

    private EnergyStack findStack() throws GetOperationFailedException {

        try {
            EnergyStack energyStack = FindEnergyStacks.find(operationStacks);

            if (finderImageParams.exists()) {
                energyStack.setParams(finderImageParams.get());
            }

            return energyStack;

        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
    }
}
