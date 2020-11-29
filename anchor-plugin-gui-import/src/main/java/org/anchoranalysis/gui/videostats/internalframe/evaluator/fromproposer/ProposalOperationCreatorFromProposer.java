/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import java.util.Set;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;

public abstract class ProposalOperationCreatorFromProposer<T> {
    private NamedProvider<T> set;

    public ProposalOperationCreatorFromProposer() {
        super();
    }

    public void init(MPPInitParams so) {
        this.set = allProposers(so);
    }

    public Set<String> keys() {
        return set.keys();
    }

    private T getItem(String itemName) throws NamedProviderGetException {
        return set.getException(itemName);
    }

    public ProposalOperationCreator createEvaluator(String itemName) throws CreateException {
        try {
            T proposer = getItem(itemName);
            return creatorFromProposer(proposer);
        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }

    public abstract ProposalOperationCreator creatorFromProposer(T proposer);

    public abstract NamedProvider<T> allProposers(MPPInitParams so);

    public abstract String getEvaluatorName();
}
