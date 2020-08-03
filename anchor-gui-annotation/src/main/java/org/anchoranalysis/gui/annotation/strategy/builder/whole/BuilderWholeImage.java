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

package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import static org.anchoranalysis.gui.videostats.internalframe.annotator.FrameActionFactory.*;

import java.util.Optional;
import javax.swing.Action;
import javax.swing.JInternalFrame;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.annotation.io.wholeimage.WholeImageLabelAnnotationWriter;
import org.anchoranalysis.annotation.wholeimage.WholeImageLabelAnnotation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.bean.label.AnnotationLabel;
import org.anchoranalysis.gui.annotation.builder.AdditionalFramesContext;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilderWithDelegate;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelWithLabel;
import org.anchoranalysis.plugin.annotation.bean.strategy.ReadAnnotationFromFile;
import org.anchoranalysis.plugin.annotation.bean.strategy.WholeImageLabelStrategy;

public class BuilderWholeImage
        extends AnnotationGuiBuilderWithDelegate<InitParamsWholeImage, WholeImageLabelStrategy> {

    private CreateAnnotationSummary createSummary;

    public BuilderWholeImage(AnnotationWithStrategy<WholeImageLabelStrategy> delegate) {
        super(delegate);

        createSummary = new CreateAnnotationSummary(new LabelColorMap(getStrategy().getLabels()));
    }

    @Override
    public InitParamsWholeImage createInitParams(
            ProgressReporterMultiple prm,
            AnnotationGuiContext context,
            Logger logger,
            boolean useDefaultCfg)
            throws CreateException {

        try {
            AnnotationBackground background =
                    createBackground(prm, stacks().get(ProgressReporterNull.get()));
    
            return new InitParamsWholeImage(background, context.getAnnotationRefresher());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public PanelNavigation createInitPanelNavigation(
            InitParamsWholeImage paramsInit,
            AnnotationPanelParams params,
            AnnotationFrameControllers controllers) {
        PanelWithLabel buttonPanel =
                new ChooseLabel(
                        () -> ReadAnnotationFromFile.readCheckExists(annotationPath()),
                        getStrategy().groupedLabels(),
                        label ->
                                saveAnnotationClose(
                                        label,
                                        createWriter(
                                                paramsInit.getAnnotationRefresher(),
                                                Optional.of(params.getSaveMonitor())),
                                        controllers.action().frame().getFrame()));

        return new PanelNavigation(buttonPanel);
    }

    @Override
    public void showAllAdditional(InitParamsWholeImage paramsInit, AdditionalFramesContext context)
            throws OperationFailedException {
        // DO NOTHING
    }

    @Override
    public boolean isUseDefaultPromptNeeded() {
        return false;
    }

    @Override
    public ExportAnnotation exportAnnotation() {
        // NOT USED
        return null;
    }

    @Override
    public int heightNonImagePanel() {
        // Formula based on observed pixel size of components in Windows
        return 15 + (35 * getStrategy().groupedLabels().numGroups());
    }

    @Override
    protected AnnotationSummary createSummary() throws CreateException {
        return createSummary.apply(annotationPath());
    }

    private Action saveAnnotationClose(
            AnnotationLabel label,
            AnnotationWriterGUI<WholeImageLabelAnnotation> writer,
            JInternalFrame parentComponent) {
        return actionCloseFrame(
                parentComponent,
                label.getUserFriendlyLabel(),
                () ->
                        writer.saveAnnotation(
                                createAnnotation(label), annotationPath(), parentComponent));
    }

    private static WholeImageLabelAnnotation createAnnotation(AnnotationLabel label) {
        return new WholeImageLabelAnnotation(label.getUniqueLabel());
    }

    private static AnnotationWriterGUI<WholeImageLabelAnnotation> createWriter(
            AnnotationRefresher annotationRefresher, Optional<SaveMonitor> saveMonitor) {
        return new AnnotationWriterGUI<>(
                new WholeImageLabelAnnotationWriter(), annotationRefresher, saveMonitor);
    }
}
