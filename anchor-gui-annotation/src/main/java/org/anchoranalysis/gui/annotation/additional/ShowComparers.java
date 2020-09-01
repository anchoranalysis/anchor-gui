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

package org.anchoranalysis.gui.annotation.additional;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.annotation.io.bean.comparer.MultipleComparer;
import org.anchoranalysis.annotation.mark.AnnotationWithMarks;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

@RequiredArgsConstructor
public class ShowComparers {

    private final ShowRaster showRaster;
    private final MultipleComparer multipleComparer;
    private final ColorSetGenerator colorSetGenerator;
    private final Path matchPath;
    private final String name;
    private final CheckedFunction<Integer, DisplayStack, ? extends Throwable> defaultBackground;
    private final Path modelDirectory;
    private final Logger logger;

    public void apply(Optional<AnnotationWithMarks> annotationExst) {
        // Any comparisons to be done
        if (multipleComparer != null && annotationExst.isPresent()) {
            showMultipleComparers(annotationExst.get());
        }
    }

    private void showMultipleComparers(AnnotationWithMarks annotationExst) {

        List<NameValue<Stack>> rasters;
        try {
            DisplayStack background = defaultBackground.apply(0);
            rasters =
                    multipleComparer.createRasters(
                            annotationExst,
                            background,
                            matchPath,
                            colorSetGenerator,
                            modelDirectory,
                            logger,
                            false);
        } catch (Exception e1) {
            logger.errorReporter().recordError(AnnotatorModuleCreator.class, e1);
            return;
        }

        for (final NameValue<Stack> ni : rasters) {

            showRaster.show(
                    progressReporter -> createBackgroundSet(ni.getValue()),
                    rasterName(ni.getName()));
        }
    }

    private String rasterName(String rasterName) {
        return String.format("%s: %s", name, rasterName);
    }

    private static BackgroundSet createBackgroundSet(Stack stack)
            throws BackgroundStackContainerException {
        BackgroundSet backgroundSet = new BackgroundSet();
        try {
            backgroundSet.addItem("Associated Raster", stack);
        } catch (OperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
        return backgroundSet;
    }
}
