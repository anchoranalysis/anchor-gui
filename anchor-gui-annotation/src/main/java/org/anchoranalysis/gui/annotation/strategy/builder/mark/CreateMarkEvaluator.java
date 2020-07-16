/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;
import org.anchoranalysis.plugin.annotation.bean.strategy.PathFromGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CreateMarkEvaluator {

    public static MarkAnnotator apply(
            MarkEvaluatorManager markEvaluatorManager,
            Path pathForBinding,
            MarkProposerStrategy strategy,
            OperationWithProgressReporter<NamedProvider<Stack>, CreateException> stacks,
            Logger logger)
            throws CreateException {

        MarkEvaluatorSetForImage set =
                createSet(markEvaluatorManager, pathForBinding, strategy, stacks);

        return new MarkAnnotator(strategy, set, logger);
    }

    public static MarkEvaluatorSetForImage createSet(
            MarkEvaluatorManager markEvaluatorManager,
            Path pathForBinding,
            MarkProposerStrategy strategy,
            OperationWithProgressReporter<NamedProvider<Stack>, CreateException> stacks)
            throws CreateException {
        return markEvaluatorManager.createSetForStackCollection(
                stacks, opLoadKeyValueParams(pathForBinding, strategy));
    }

    private static Operation<Optional<KeyValueParams>, IOException> opLoadKeyValueParams(
            Path pathForBinding, MarkProposerStrategy strategy) {
        return () -> paramsFromGenerator(pathForBinding, strategy.paramsFilePathGenerator());
    }

    private static Optional<KeyValueParams> paramsFromGenerator(
            Path pathForBinding, Optional<FilePathGenerator> generator) throws IOException {
        return OptionalUtilities.map(
                generator,
                gen -> {
                    try {
                        return KeyValueParams.readFromFile(
                                PathFromGenerator.derivePath(gen, pathForBinding));
                    } catch (AnchorIOException e) {
                        throw new IOException(e);
                    }
                });
    }
}
