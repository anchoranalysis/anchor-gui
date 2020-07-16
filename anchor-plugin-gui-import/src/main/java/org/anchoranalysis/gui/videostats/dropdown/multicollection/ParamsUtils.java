/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.multicollection;

import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.params.KeyValueParams;

class ParamsUtils {

    private ParamsUtils() {}

    public static KeyValueParams apply(
            NamedProviderStore<KeyValueParams> paramsCollection, ErrorReporter errorReporter) {
        if (paramsCollection.keys().size() > 1) {

            // TODO change
            // HARDCORE a default for when we have more than one
            try {
                Optional<KeyValueParams> params = paramsCollection.getOptional("input_params");
                if (params.isPresent()) {
                    return params.get();
                }
            } catch (NamedProviderGetException e) {
                errorReporter.recordError(ParamsUtils.class, e.summarize());
            }

            errorReporter.recordError(
                    ParamsUtils.class,
                    new OperationFailedException(
                            "More than one params. Cannot choose between them."));
            return new KeyValueParams();
        } else if (paramsCollection.keys().size() == 1) {
            try {
                return paramsCollection.getException(paramsCollection.keys().iterator().next());
            } catch (NamedProviderGetException e) {
                errorReporter.recordError(ParamsUtils.class, e.summarize());
                return new KeyValueParams();
            }
        } else {
            return new KeyValueParams();
        }
    }
}
