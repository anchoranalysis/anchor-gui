/* (C)2020 */
package org.anchoranalysis.gui.annotation.additional;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.annotation.io.bean.comparer.MultipleComparer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
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
    private final FunctionWithException<Integer, DisplayStack, ? extends Throwable>
            defaultBackground;
    private final Path modelDirectory;
    private final Logger logger;

    public void apply(Optional<AnnotationWithCfg> annotationExst) {
        // Any comparisons to be done
        if (multipleComparer != null && annotationExst.isPresent()) {
            showMultipleComparers(annotationExst.get());
        }
    }

    private void showMultipleComparers(AnnotationWithCfg annotationExst) {

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
            throws GetOperationFailedException {
        BackgroundSet backgroundSet = new BackgroundSet();
        try {
            backgroundSet.addItem("Associated Raster", stack);
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
        return backgroundSet;
    }
}
