/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

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
    @BeanField
    private GeneratorSequenceFactory sequenceFactory = new SubfolderGeneratorSequenceFactory();

    @BeanField private CreateRasterGenerator<T> createRasterGenerator;

    @BeanField private String outputName = "defaultOutputName";
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

    public GeneratorSequenceFactory getSequenceFactory() {
        return sequenceFactory;
    }

    public void setSequenceFactory(GeneratorSequenceFactory sequenceFactory) {
        this.sequenceFactory = sequenceFactory;
    }

    public CreateRasterGenerator<T> getCreateRasterGenerator() {
        return createRasterGenerator;
    }

    public void setCreateRasterGenerator(CreateRasterGenerator<T> createRasterGenerator) {
        this.createRasterGenerator = createRasterGenerator;
    }

    @Override
    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
}
