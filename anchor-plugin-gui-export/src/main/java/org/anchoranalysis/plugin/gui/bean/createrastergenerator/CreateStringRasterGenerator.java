/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.io.generator.raster.StringRasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public abstract class CreateStringRasterGenerator
        extends CreateRasterGenerator<CfgNRGInstantState> {

    // START BEAN PROPERTIES
    @BeanField private StringRasterGenerator stringGenerator;
    // END BEAN PROPERTIES

    @Override
    public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        return new IterableObjectGeneratorBridge<Stack, MappedFrom<CfgNRGInstantState>, String>(
                stringGenerator.createGenerator(),
                sourceObject -> extractStringFrom(sourceObject.getObj().getCfgNRG()));
    }

    protected abstract String extractStringFrom(CfgNRG cfgNRG);

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return true;
    }

    public StringRasterGenerator getStringGenerator() {
        return stringGenerator;
    }

    public void setStringGenerator(StringRasterGenerator stringGenerator) {
        this.stringGenerator = stringGenerator;
    }
}
