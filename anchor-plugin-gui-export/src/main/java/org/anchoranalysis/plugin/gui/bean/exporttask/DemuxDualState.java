/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;

public class DemuxDualState<T> extends CreateRasterGenerator<DualStateWithoutIndex<T>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int index = 0;

    @BeanField @Getter @Setter private CreateRasterGenerator<T> item;
    // END BEAN PROPERTIES

    private T demux(DualStateWithoutIndex<T> in) {
        return in.getItem(index);
    }

    @Override
    public IterableObjectGenerator<MappedFrom<DualStateWithoutIndex<T>>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        IterableObjectGenerator<MappedFrom<T>, Stack> generator = item.createGenerator(params);

        return new IterableObjectGeneratorBridge<
                Stack, MappedFrom<DualStateWithoutIndex<T>>, MappedFrom<T>>(
                generator,
                sourceObject ->
                        new MappedFrom<>(
                                sourceObject.getOriginalIter(), demux(sourceObject.getObj())));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return true;
    }
}
