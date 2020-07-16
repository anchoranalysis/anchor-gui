/* (C)2020 */
package org.anchoranalysis.gui.annotation.builder;

import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.BuilderProposeMarks;
import org.anchoranalysis.gui.annotation.strategy.builder.whole.BuilderWholeImage;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;
import org.anchoranalysis.plugin.annotation.bean.strategy.WholeImageLabelStrategy;

public class AnnotationGuiBuilderFactory {

    @SuppressWarnings("unchecked")
    public static AnnotationGuiBuilder<?> create(AnnotationWithStrategy<?> annotation)
            throws CreateException {
        AnnotatorStrategy strategy = annotation.getStrategy();
        try {
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
        } catch (AnchorIOException e) {
            throw new CreateException(e);
        }
    }
}
