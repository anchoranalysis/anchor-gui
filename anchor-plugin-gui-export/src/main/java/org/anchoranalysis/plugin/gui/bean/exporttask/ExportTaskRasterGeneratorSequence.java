/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.io.generator.bean.sequence.factory.GeneratorSequenceFactory;
import org.anchoranalysis.io.generator.bean.sequence.factory.SubfolderGeneratorSequenceFactory;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncremental;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;

public abstract class ExportTaskRasterGeneratorSequence<T> extends ExportTaskBean {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter
    private GeneratorSequenceFactory sequenceFactory = new SubfolderGeneratorSequenceFactory();

    @BeanField @Getter @Setter private CreateRasterGenerator<T> createRasterGenerator;

    @BeanField @Setter private String outputName = "defaultOutputName";
    // END BEAN PARAMETERS

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return createRasterGenerator.hasNecessaryParams(params);
    }

    protected GeneratorSequenceNonIncremental<MappedFrom<T>> createGeneratorSequenceWriter(
            ExportTaskParams params) throws CreateException {

        return getSequenceFactory()
                .createGeneratorSequenceNonIncremental(
                        params.getOutputManager(),
                        getOutputName(),
                        getCreateRasterGenerator().createGenerator(params));
    }

    @Override
    public String getOutputName() {
        return outputName;
    }
}
