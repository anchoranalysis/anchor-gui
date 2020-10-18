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

package org.anchoranalysis.gui.annotation.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.BuilderProposeMarks;
import org.anchoranalysis.gui.annotation.strategy.builder.whole.BuilderWholeImage;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;
import org.anchoranalysis.plugin.annotation.bean.strategy.WholeImageLabelStrategy;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationGuiBuilderFactory {

    @SuppressWarnings("unchecked")
    public static AnnotationGuiBuilder<?> create(AnnotationWithStrategy<?> annotation)
            throws CreateException {
        AnnotatorStrategy strategy = annotation.getStrategy();

        // UGLY hard-coding of mapping between AnnotationWithStrategy to an AnnotationGuiBuilder
        if (strategy instanceof MarkProposerStrategy) {
            return new BuilderProposeMarks(
                    (AnnotationWithStrategy<MarkProposerStrategy>) annotation);

        } else if (strategy instanceof WholeImageLabelStrategy) {
            return new BuilderWholeImage(
                    (AnnotationWithStrategy<WholeImageLabelStrategy>) annotation);

        } else {
            throw new CreateException(
                    String.format(
                            "Unsupported annotation-strategy for %s",
                            annotation.getStrategy().getClass()));
        }
    }
}
