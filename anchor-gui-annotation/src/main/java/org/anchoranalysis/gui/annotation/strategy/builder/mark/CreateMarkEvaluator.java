/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;
import org.anchoranalysis.io.input.bean.path.DerivePath;
import org.anchoranalysis.io.input.path.DerivePathException;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;
import org.anchoranalysis.plugin.annotation.bean.strategy.PathFromGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CreateMarkEvaluator {

    public static MarkAnnotator apply(
            MarkEvaluatorManager markEvaluatorManager,
            Path pathForBinding,
            MarkProposerStrategy strategy,
            NamedStacksSupplier stacks,
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
            NamedStacksSupplier stacks)
            throws CreateException {
        return markEvaluatorManager.createSetForStackCollection(
                stacks, () -> paramsFromGenerator(pathForBinding, strategy.paramsDeriver()));
    }

    private static Optional<Dictionary> paramsFromGenerator(
            Path pathForBinding, Optional<DerivePath> derivePath) throws IOException {
        return OptionalUtilities.map(
                derivePath, generator -> readParams(generator, pathForBinding));
    }

    private static Dictionary readParams(DerivePath derivePath, Path pathForBinding)
            throws IOException {
        try {
            return Dictionary.readFromFile(
                    PathFromGenerator.derivePath(derivePath, pathForBinding));
        } catch (DerivePathException e) {
            throw new IOException(e);
        }
    }
}
