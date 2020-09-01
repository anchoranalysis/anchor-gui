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

package org.anchoranalysis.gui.annotation;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.dropdown.AnnotationDropDown;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class FileAnnotationNamedChannels extends InteractiveFile {

    private AnnotationGuiBuilder<?> annotation;
    private AnnotationRefresher annotationRefresher;
    private VideoStatsModuleGlobalParams mpg;
    private MarkEvaluatorManager markEvaluatorManager;

    private CachedSupplier<AnnotationSummary, ? extends Throwable> op;

    public FileAnnotationNamedChannels(
            AnnotationGuiBuilder<?> annotation,
            AnnotationRefresher annotationRefresher,
            MarkEvaluatorManager markEvaluatorManager,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.annotation = annotation;
        this.annotationRefresher = annotationRefresher;
        this.mpg = mpg;
        this.markEvaluatorManager = markEvaluatorManager;

        op = annotation.queryAnnotationSummary();

        try {
            op.get();
        } catch (Exception e) {
            mpg.getLogger().errorReporter().recordError(FileAnnotationNamedChannels.class, e);
        }
    }

    @Override
    public String identifier() {
        return annotation.descriptiveName();
    }

    private void invalidateProgressState() {
        op.reset();
    }

    public AnnotationSummary summary() {
        try {
            return op.get();
        } catch (Exception e) {
            mpg.getLogger().errorReporter().recordError(FileAnnotationNamedChannels.class, e);
            return null;
        }
    }

    @Override
    public String type() {
        return FileAnnotationNamedChannels.class.getSimpleName();
    }

    @Override
    public OpenedFile open(
            AddVideoStatsModule globalSubgroupAdder, BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException {

        AnnotationRefresher refresherResetCache =
                () -> {
                    invalidateProgressState();
                    annotationRefresher.refreshAnnotation();
                };

        AnnotationDropDown dropDown =
                new AnnotationDropDown(
                        annotation,
                        new AnnotationGuiContext(refresherResetCache, markEvaluatorManager),
                        identifier());

        dropDown.init(globalSubgroupAdder, outputManager, mpg);

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }

    @Override
    public Optional<File> associatedFile() {
        return annotation.associatedFile();
    }
}
