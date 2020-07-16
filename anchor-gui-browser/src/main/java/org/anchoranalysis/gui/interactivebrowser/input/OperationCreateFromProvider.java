/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.input;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;

class OperationCreateFromProvider<ProviderType>
        extends CachedOperation<ProviderType, OperationFailedException> {

    private Provider<ProviderType> provider;

    public OperationCreateFromProvider(Provider<ProviderType> provider) {
        super();
        this.provider = provider;
    }

    @Override
    protected ProviderType execute() throws OperationFailedException {
        try {
            return provider.create();
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
