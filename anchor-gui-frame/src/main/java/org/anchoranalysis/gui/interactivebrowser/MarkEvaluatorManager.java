/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.MarkEvaluator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

// Manages the various MarkEvaluators that are available in the application
public class MarkEvaluatorManager {

    private Map<String, MarkEvaluator> map = new HashMap<>();

    private BoundIOContext context;

    public MarkEvaluatorManager(BoundIOContext context) {
        super();
        this.context = context;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public MarkEvaluatorSetForImage createSetForStackCollection(
            OperationWithProgressReporter<NamedProvider<Stack>, ? extends Throwable>
                    namedImgStackCollection,
            Operation<Optional<KeyValueParams>, IOException> keyParams)
            throws CreateException {

        try {
            MarkEvaluatorSetForImage out =
                    new MarkEvaluatorSetForImage(namedImgStackCollection, keyParams, context);

            for (String key : map.keySet()) {
                MarkEvaluator me = map.get(key);
                out.add(key, me.duplicateBean());
            }
            return out;

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public void add(String key, MarkEvaluator item) {
        map.put(key, item);
    }

    public boolean hasItems() {
        return map.size() > 0;
    }
}
