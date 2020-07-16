/* (C)2020 */
package org.anchoranalysis.gui.bean.exporttask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;

@AllArgsConstructor
public class ExportTaskGenerator<T> implements ExportTask {

    private final IterableGenerator<T> generator;
    private final T item;
    private final JFrame parentFrame;
    private final IndexableOutputNameStyle outputNameStyle;
    private final SequenceMemory sequenceMemory;

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return true;
    }

    @Override
    public boolean execute(ExportTaskParams params, ProgressMonitor progressMonitor)
            throws ExportTaskFailedException {

        int index = sequenceMemory.lastIndex(outputNameStyle.getOutputName());

        try {
            generator.setIterableElement(item);
        } catch (SetOperationFailedException e) {
            throw new ExportTaskFailedException(e);
        }

        int numWritten =
                params.getOutputManager()
                        .getWriterCheckIfAllowed()
                        .write(outputNameStyle, generator::getGenerator, index);
        sequenceMemory.updateIndex(outputNameStyle.getOutputName(), index + numWritten);

        if (numWritten == 0) {
            JOptionPane.showMessageDialog(parentFrame, "An error occurred");
        }

        return true;
    }

    @Override
    public String getBeanName() {
        return ExportTaskGenerator.class.getSimpleName();
    }

    @Override
    public int getMinProgress(ExportTaskParams params) throws ExportTaskFailedException {
        return 0;
    }

    @Override
    public int getMaxProgress(ExportTaskParams params) throws ExportTaskFailedException {
        return 0;
    }

    @Override
    public String getOutputName() {
        return outputNameStyle.getOutputName();
    }

    @Override
    public void init() {
        // NOTHING TO DO
    }
}
