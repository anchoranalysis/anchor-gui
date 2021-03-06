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

package org.anchoranalysis.gui.interactivebrowser;

import lombok.Getter;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;

/**
 * A {@link MarkEvaluator} after it has been resolved for usage.
 *
 * @author Owen Feehan
 */
public class MarkEvaluatorResolved {

    private final CachedSupplier<MarksInitialization, CreateException> supplyInitialization;
    private final CachedSupplier<EnergyStack, CreateException> operationCreateEnergyStack;

    @Getter private final MarkWithIdentifierFactory markFactory;

    @Getter private final EnergyScheme energyScheme;

    public MarkEvaluatorResolved(
            CachedSupplier<MarksInitialization, CreateException> supplyInitialization,
            MarkWithIdentifierFactory markFactory,
            EnergyScheme energyScheme,
            Dictionary dictionary) {
        this.supplyInitialization = supplyInitialization;
        this.markFactory = markFactory;
        this.energyScheme = energyScheme;

        this.operationCreateEnergyStack =
                CachedSupplier.cache(
                        () -> CreateEnergyStackHelper.create(supplyInitialization, dictionary));
    }

    public CachedSupplier<MarksInitialization, CreateException>
            getProposerSharedObjectsOperation() {
        return supplyInitialization;
    }

    public EnergyStack getEnergyStack() throws OperationFailedException {
        try {
            return operationCreateEnergyStack.get();
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
