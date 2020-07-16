/* (C)2020 */
package org.anchoranalysis.gui.retrieveelements;

import java.util.Optional;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.bean.exporttask.ExportTask;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread.ExportTaskCommand;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskGenerator;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableGenerator;
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
    public void addExportItemStackGenerator(
            String outputName, String label, Operation<Stack, AnchorNeverOccursException> stack)
            throws OperationFailedException {

        StackGenerator stackGenerator = new StackGenerator(true, outputName);
        OperationGenerator<Stack, Stack> generator = new OperationGenerator<>(stackGenerator);
        addExportItem(
                generator, stack, outputName, label, generator.createManifestDescription(), 1);
    }

    @Override
    public <T> void addExportItem(
            IterableGenerator<T> generator,
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
