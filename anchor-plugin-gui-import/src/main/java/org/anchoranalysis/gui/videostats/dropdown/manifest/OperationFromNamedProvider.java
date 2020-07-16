/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.manifest;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;

class OperationFromNamedProvider<T>
        extends CachedOperationWithProgressReporter<T, OperationFailedException> {

    private NamedProvider<T> provider;
    private String name;

    public OperationFromNamedProvider(NamedProvider<T> provider, String name) {
        super();
        this.provider = provider;
        this.name = name;
    }

    @Override
    protected T execute(ProgressReporter progressReporter) throws OperationFailedException {
        try {
            return provider.getException(name);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e);
        }
    }
}
