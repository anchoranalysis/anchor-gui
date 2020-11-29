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

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.gui.annotation.AnnotationBackgroundInstance;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackground;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationInitParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.image.core.stack.named.NamedStacksSupplier;

public abstract class AnnotationGuiBuilder<T extends AnnotationInitParams> {

    private CachedSupplier<AnnotationSummary, CreateException> queryAnnotationStatus;

    public AnnotationGuiBuilder() {
        super();
        this.queryAnnotationStatus = createCachedSummary();
    }

    /** Should the user be prompted to use a default annotation? */
    public abstract boolean isUseDefaultPromptNeeded();

    public abstract T createInitParams(
            ProgressMultiple progressMultiple,
            AnnotationGuiContext context,
            Logger logger,
            boolean useDefaultMarks)
            throws CreateException;

    /**
     * A mechanism for exporting annotations
     *
     * @return mechanism or null if it isn't supported
     */
    public abstract ExportAnnotation exportAnnotation();

    public abstract PanelNavigation createInitPanelNavigation(
            T paramsInit, AnnotationPanelParams params, AnnotationFrameControllers controllers);

    public abstract void showAllAdditional(T paramsInit, AdditionalFramesContext context)
            throws OperationFailedException;

    /**
     * A cached annotation-summary for the annotation
     *
     * <p>The cache should be reset of the annotation is changed by the user
     */
    public CachedSupplier<AnnotationSummary, CreateException> queryAnnotationSummary() {
        return queryAnnotationStatus;
    }

    public abstract ChangeableBackground backgroundDefinition(
            AnnotationBackgroundInstance annotationBackground);

    /** A path that is used to allow the user to delete an annotation */
    public abstract Path deletePath();

    // Cached-operation
    public abstract NamedStacksSupplier stacks();

    public abstract String inputName();

    public abstract Optional<File> associatedFile();

    /** Height in pixels of the panel that isn't the 'image' section of the frame */
    public abstract int heightNonImagePanel();

    /** Creates an annotation summary for the current-annotation */
    protected abstract AnnotationSummary createSummary() throws CreateException;

    /** Creates a cached version of the createSummary() method */
    private CachedSupplier<AnnotationSummary, CreateException> createCachedSummary() {
        return CachedSupplier.cache(this::createSummary);
    }
}
