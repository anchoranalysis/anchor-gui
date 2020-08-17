package org.anchoranalysis.gui.interactivebrowser;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.params.KeyValueParams;

public interface KeyValueParamsSupplier {

    Optional<KeyValueParams> get() throws IOException;

    public static KeyValueParamsSupplier cache(KeyValueParamsSupplier supplier) {
        return CachedSupplier.cache(supplier::get)::get;
    }
}
