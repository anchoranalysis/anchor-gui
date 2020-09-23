/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.retrieveelements;

import java.util.Optional;
import java.util.function.Supplier;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.export.bean.ExportTask;
import org.anchoranalysis.gui.export.bean.ExportTaskActionAsThread;
import org.anchoranalysis.gui.export.bean.ExportTaskGenerator;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.gui.export.bean.ExportTaskActionAsThread.ExportTaskCommand;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@RequiredArgsConstructor
public class ExportSubMenu implements AddToExportSubMenu {

    private final ExportPopupParams params;
    private final ErrorReporter errorReporter;

    private JMenu menu = new JMenu("Export");

    @Override
    public void addExportItemStackGenerator(String outputName, String label, Supplier<Stack> stack)
            throws OperationFailedException {

        StackGenerator stackGenerator = new StackGenerator(true, outputName, false);
        SupplierGenerator<Stack, Stack> generator = new SupplierGenerator<>(stackGenerator);
        addExportItem(
                generator, stack, outputName, label, generator.createManifestDescription(), 1);
    }

    @Override
    public <T> void addExportItem(
            Generator<T> generator,
            T itemToGenerate,
            String outputName,
            String label,
            Optional<ManifestDescription> md,
            int numItems) {

        // No parameters available in this context
        ExportTaskParams exportTaskParams = new ExportTaskParams();

        ManifestFolderDescription mfd =
                new ManifestFolderDescription(
                        md.orElseThrow(
                                () ->
                                        new AnchorFriendlyRuntimeException(
                                                "A manifest-description is required for this operation")),
                        new IncrementalSequenceType());

        // NB: As bindAsSubFolder can now return nulls, maybe some knock-on bugs are introduced here
        exportTaskParams.setOutputManager(
                params.getOutputManager()
                        .getWriterAlwaysAllowed()
                        .bindAsSubdirectory(outputName, mfd)
                        .orElseThrow(
                                () ->
                                        new AnchorFriendlyRuntimeException(
                                                "No subdirectory can be created for output")));

        IntegerSuffixOutputNameStyle ons = new IntegerSuffixOutputNameStyle(outputName, 6);

        ExportTask task =
                new ExportTaskGenerator<T>(
                        generator,
                        itemToGenerate,
                        params.getParentFrame(),
                        ons,
                        params.getSequenceMemory());

        ExportTaskActionAsThread action =
                new ExportTaskActionAsThread(
                        label,
                        new ExportTaskCommand(
                                task,
                                exportTaskParams,
                                params.getParentFrame(),
                                false,
                                errorReporter));
        menu.add(new JMenuItem(action));
    }

    public JMenu getMenu() {
        return menu;
    }

    @Override
    public BoundOutputManagerRouteErrors getOutputManager() {
        return params.getOutputManager();
    }
}
